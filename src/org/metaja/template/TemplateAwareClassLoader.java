package org.metaja.template;

import org.metaja.compiler.JavaAwareClassLoader;
import org.metaja.utils.ClassUtils;
import org.metaja.utils.EscapedWriter;
import org.metaja.utils.ResourceUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.of;

public class TemplateAwareClassLoader extends JavaAwareClassLoader {

    private static final boolean DEBUG_CODE = Boolean.getBoolean(TemplateAwareClassLoader.class.getName() + ".DEBUG_CODE");
    private static final boolean DEBUG_GEN = Boolean.getBoolean(TemplateAwareClassLoader.class.getName() + ".DEBUG_GEN");

    private Map<Object, String[]> cache = new HashMap<>();
    private Map<String, Integer> cacheOfIds = new HashMap<>();

    public TemplateAwareClassLoader(ClassLoader parent) {
        super(parent);
    }

    public synchronized String[] compile(String templateFileName, String templateFileContent, Object... args) throws IllegalArgumentException {
        try {
            Generator generator = createGenerator(templateFileContent);
            String javaCode = generator.generate(this, args);
            String[] compiledClassesNames = compile(templateFileName, javaCode);

            if (DEBUG_CODE) {
                System.out.println("------ result code ------");
                System.out.println(formatCode(javaCode));
                System.out.println("-------------------------");
            }

            return compiledClassesNames;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Can not compile template with args ("
                    + stream(args).map(o -> o == null ? "null" : o.getClass().getName()).collect(joining(", "))
                    + "):\n" + formatCode(templateFileContent), e);
        }
    }

    public synchronized String[] compile(String template, Object... args) throws IllegalArgumentException {
        return compile(null, template, args);
    }

    public synchronized String[] compileResource(String path, String charsetName, boolean argId, Object... args) {
        Charset charset = Charset.forName(charsetName);
        Object key = new ArrayList<>(Arrays.asList(path, charset, new ArrayList<>(Arrays.asList(args))));
        String[] compiledClasses = cache.get(key);
        if (compiledClasses == null) {
            if (argId) { // tricky way to have one template for group of classes
                Integer id = cacheOfIds.get(path);
                id = id == null ? 1 : id + 1;
                cacheOfIds.put(path, id);
                Object[] argsNew = new Object[args.length + 1];
                argsNew[0] = id;
                System.arraycopy(args, 0, argsNew, 1, args.length);
                args = argsNew;
            }
            compiledClasses = compile(ResourceUtils.readResource(this, path, charset), args);
            cache.put(key, compiledClasses);
        }
        return compiledClasses;
    }

    // ====== private ======

    private long generatorsCounter = 1;
    private final JavaAwareClassLoader generatorsClassLoader = new JavaAwareClassLoader(ClassLoader.getSystemClassLoader());

    public static class Generator implements TemplateOps {
        public static final String DO_GENERATE_METHOD_NAME = "doGenerate";

        private StringWriter s = new StringWriter();
        private PrintWriter w = new PrintWriter(s);
        private int line = 1;
        private TemplateAwareClassLoader classLoader;

        public final synchronized String generate(TemplateAwareClassLoader classLoader, Object ... args) {
            try {
                this.classLoader = classLoader;

                for (Method method : getClass().getMethods()) {
                    if (method.getName().equals(DO_GENERATE_METHOD_NAME)) {
                        method.invoke(this, args);
                    }
                }
                return s.toString();

            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        }

        // === ops ===

        public final void OUT(Object o) {
            w.print(o);
        }

        public final void OUT(int line, Object o) {
            for (int i = this.line; i < line;i++, this.line++) {
                OUT('\n');
            }
            OUT(o);
        }

        protected final String IMPORT_DYN(String templatePath, String charsetName, boolean argId, Object ... args) {
            return String.format("((%s)getClass().getClassLoader()).compileResource(\"%s\", \"%s\", %s %s)",
                    TemplateAwareClassLoader.class.getName(),
                    templatePath,
                    charsetName,
                    Boolean.toString(argId),
                    stream(args).flatMap(arg -> of(", ", valueOf(arg))).collect(joining())
            );
        }

        protected final String[] IMPORT(String templatePath, String charsetName, Object ... args) {
            return classLoader.compileResource(templatePath, charsetName, false, args);
        }

        protected final String NEW(String className, Object ... args) {
            return String.format("%s.newInstance(%s.<%s>loadClass(this.getClass().getClassLoader(), \"%s\") %s);",
                    ClassUtils.class.getName(),
                    ClassUtils.class.getName(),
                    className,
                    className,
                    stream(args).flatMap(arg -> of(", ", valueOf(arg))).collect(joining())
            );
        }

        protected final String NEW_DYN(String className, String resultType, Object ... args) {
            return String.format("%s.newInstance(%s.<%s>loadClass(this.getClass().getClassLoader(), %s) %s);",
                    ClassUtils.class.getName(),
                    ClassUtils.class.getName(),
                    resultType,
                    className,
                    stream(args).flatMap(arg -> of(", ", valueOf(arg))).collect(joining())
            );
        }
    }

    private Generator createGenerator(String template) {
        try {
            String generatorClassName = Generator.class.getSimpleName() + "_" + generatorsCounter++;
            String generatorPackageName = TemplateAwareClassLoader.class.getPackage().getName();

            String generatorSource = String.format(
                    "package %s; public class %s extends %s.%s { public void %s %s }",
                    generatorPackageName,
                    generatorClassName,
                    TemplateAwareClassLoader.class.getSimpleName(),
                    Generator.class.getSimpleName(),
                    Generator.DO_GENERATE_METHOD_NAME,

                    TemplateParser.parse(template, new TemplateParser.TemplateWriter<IOException>() {
                        private final StringWriter out = new StringWriter();
                        private final EscapedWriter outE = new EscapedWriter(out);
                        private WriteType type;
                        private int line = 1;

                        @Override
                        public void write(char c) throws IOException {
                            if (c == '\n') {
                                line++;
                            }
                            if (type == WriteType.OUT || type == WriteType.MACRO_OUT) {
                                if (c == '\n') {
                                    out.write("\");\nOUT(" + line + ", \" ");
                                } else {
                                    outE.write(c);
                                }
                            } else {
                                out.write(c);
                            }
                        }

                        @Override
                        public void setWriteType(WriteType newType) throws IOException {
                            if (type == WriteType.OUT || type == WriteType.MACRO_OUT) {
                                out.write("\");");
                            } else if (type == WriteType.MACRO_VALUE) {
                                out.write(");");
                            }
                            if (newType != WriteType.MACRO) {
                                if (type == WriteType.MACRO) {
                                    out.write("OUT(" + line + ", ");
                                } else {
                                    out.write("OUT(");
                                }
                                if (newType == WriteType.OUT || newType == WriteType.MACRO_OUT) {
                                    out.write('\"');
                                }
                            }
                            type = newType;
                        }

                        @Override
                        public String toString() {
                            return out.toString();
                        }
                    })
            );

            generatorsClassLoader.compile(generatorSource);

            if (DEBUG_GEN) {
                System.out.println("---- generators code -----");
                System.out.println(formatCode(generatorSource));
                System.out.println("--------------------------");
            }

            @SuppressWarnings("unchecked")
            Class<Generator> generatorClass = (Class<Generator>) generatorsClassLoader.loadClass(generatorPackageName + '.' + generatorClassName);

            return generatorClass.newInstance();

        } catch (IOException e) {
            throw new Error("Can not happen!", e);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }
}

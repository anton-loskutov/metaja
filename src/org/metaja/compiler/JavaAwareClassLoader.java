package org.metaja.compiler;

import org.metaja.utils.ResourceUtils;
import com.sun.tools.javac.api.ClientCodeWrapper;

import javax.tools.*;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class JavaAwareClassLoader extends ClassLoader {

    private static final boolean NO_DEBUG_INFO = Boolean.getBoolean(JavaAwareClassLoader.class.getName() + ".NO_DEBUG_CODE");

    private final Map<String, Map<String, JavaClass>> compiled = new HashMap<>();

    public JavaAwareClassLoader(ClassLoader parent) {
        super(parent);
    }

    public synchronized String[] compile(String javaSourceStr) throws IllegalArgumentException {
        return compile(null, javaSourceStr);
    }

    public synchronized String[] compile(String javaFileName, String javaFileContent) throws IllegalArgumentException {
        final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();
        final HashSet<String> compiledClassesNames = new HashSet<>();
        final JavaFileManager manager = new ForwardingJavaFileManager<JavaFileManager>(javac.getStandardFileManager(diagnosticCollector, null, null)) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String classNameStr, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
                compiledClassesNames.add(classNameStr);
                return getJavaClass(new JavaClassName(classNameStr), true);
            }

            @Override
            public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
                if (location == StandardLocation.PLATFORM_CLASS_PATH) {
                    return super.list(location, packageName, kinds, recurse);
                }
                if (!kinds.contains(JavaFileObject.Kind.CLASS)) {
                    return Collections.emptyList();
                }
                if (recurse) {
                    throw new UnsupportedOperationException();
                }
                Map<String, JavaFileObject> files = new HashMap<>();
                for (JavaFileObject javaFileObject : super.list(location, packageName, Collections.singleton(JavaFileObject.Kind.CLASS), false)) {
                    files.put(super.inferBinaryName(location, javaFileObject), javaFileObject);
                }
                for (JavaClass javaClass : getJavaClasses(packageName)) {
                    files.put(javaClass.className.full, javaClass);
                }
                return files.values();
            }

            @Override
            public String inferBinaryName(Location location, JavaFileObject file) {
                if (file instanceof JavaClass) {
                    return ((JavaClass) file).className.full;
                }
                return super.inferBinaryName(location, file);
            }

            @Override
            public ClassLoader getClassLoader(Location location) {
                return JavaAwareClassLoader.this;
            }
        };

        final StringWriter compilationOut = new StringWriter();
        final List<JavaSource> compilationUnits = Collections.singletonList(new JavaSource(javaFileName == null ? "" : javaFileName, javaFileContent));
        final List<String> compilationOptions = new LinkedList<>();
        if (!NO_DEBUG_INFO) {
            compilationOptions.add("-g");
        }
        final JavaCompiler.CompilationTask task = javac.getTask(compilationOut, manager, diagnosticCollector, compilationOptions, null, compilationUnits);
        if (!task.call()) {
            StringBuilder errors = new StringBuilder();
            for (Diagnostic<? extends JavaFileObject> diag : diagnosticCollector.getDiagnostics()) {
                if (javaFileName == null && diag.getCode().equals("compiler.err.class.public.should.be.in.file")) {
                    return compile(((ClientCodeWrapper.DiagnosticSourceUnwrapper) diag).d.getArgs()[0] + ".java", javaFileContent);
                }
                if (diag.getKind() == Diagnostic.Kind.ERROR || diag.getKind() == Diagnostic.Kind.MANDATORY_WARNING) {
                    errors.append(String.format("[line: %d, column: %d]: '%s'\n",
                            diag.getLineNumber(),
                            diag.getColumnNumber(),
                            diag.getMessage(Locale.US)
                    ));
                }
            }
            String outStr = compilationOut.toString();
            if (errors.length() == 0) {
                throw new Error(
                        "Can not compile for unknown reason!\n-------\n" +
                                formatCode(javaFileContent) + "\n-------\n" +
                                (outStr.isEmpty() ? "" : outStr + "-------\n"));
            }
            throw new IllegalArgumentException(
                    "Can not compile!\n-------\n" +
                            formatCode(javaFileContent) + "\n-------\n" +
                            (outStr.isEmpty() ? "" : outStr + "-------\n") +
                            errors.toString() + "-------");
        }
        return compiledClassesNames.toArray(new String[compiledClassesNames.size()]);
    }

    public synchronized Class<?> reloadClass(Class<?> clazz) {
        try {
            JavaClassName className = new JavaClassName(clazz.getName());
            Map<String, JavaClass> pack = compiled.get(className.pack);
            if (pack == null || pack.remove(className.full) == null) {
                return loadClass(className.full);
            }
            String classPath = className.full.replace('.', '/') + ".class";
            byte[] classByteCode = ResourceUtils.readResource(this, classPath);
            if (classByteCode == null) {
                throw new IllegalArgumentException("Can not find class file for '" + clazz.getName() + "'!");
            }
            defineClass(clazz.getName(), classByteCode, 0, classByteCode.length);
            return loadClass(className.full);
        } catch (ResourceUtils.IORuntimeException | ClassNotFoundException e) {
            throw new Error(e);
        }
    }

    // ======= private =======

    private JavaClass getJavaClass(JavaClassName className, boolean forceCreate) {
        Map<String, JavaClass> compiledInPackage = compiled.get(className.pack);
        if (compiledInPackage == null) {
            compiledInPackage = new HashMap<>();
            compiled.put(className.pack, compiledInPackage);
        }
        JavaClass javaClass = compiledInPackage.get(className.full);
        if (javaClass == null && forceCreate) {
            javaClass = new JavaClass(className) {
                @Override
                protected void onByteCodeUpdated(JavaClass javaClass) {
                    byte[] byteCode = javaClass.getByteCode();
                    defineClass(javaClass.className.full, byteCode, 0, byteCode.length);
                }
            };
            compiledInPackage.put(className.full, javaClass);
        }
        return javaClass;
    }

    private Collection<? extends JavaClass> getJavaClasses(String packageName) {
        Map<String, JavaClass> compiledInPackage = compiled.get(packageName);
        if (compiledInPackage == null) {
            return Collections.emptyList();
        }
        return compiledInPackage.values();
    }

    // ======= public static =======

    public static String formatCode(String code) {
        StringBuilder codeFormatted = new StringBuilder();
        String[] codeLines = code.split("\\n");
        int l = Math.max(1, (int) Math.ceil(Math.log10(codeLines.length)));
        for (int i = 0; i < codeLines.length; i++) {
            codeFormatted.append(String.format("%1$" + l + "d | %2$s\n", i + 1, codeLines[i]));
        }
        codeFormatted.setLength(codeFormatted.length() - 1);
        return codeFormatted.toString();
    }
}

package org.metaja.compiler;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaCompileException extends RuntimeException {

    public final Supplier<Stream<JavaCompileError>> errors;

    public JavaCompileException(String message, Iterable<? extends Diagnostic<? extends JavaFileObject>> compileDiagnostics) {
        super(message.isEmpty() ? "Can not compile java code!" : "Can not compile java code! (" + message + ")");
        ArrayList<JavaCompileError> errors = new ArrayList<>();
        for (Diagnostic<? extends JavaFileObject> compileDiagnostic : compileDiagnostics) {
            if (compileDiagnostic.getKind() == Diagnostic.Kind.ERROR || compileDiagnostic.getKind() == Diagnostic.Kind.MANDATORY_WARNING) {
                errors.add(new JavaCompileError(
                        compileDiagnostic.getLineNumber(),
                        compileDiagnostic.getColumnNumber(),
                        compileDiagnostic.getMessage(Locale.US)
                ));
            }
        }
        this.errors = errors::stream;
    }

    public JavaCompileException initDetailsWithFileName(String fileName) {
        initCause(new JavaCompileException_DETAILS(
                "\n-------------------------\n" +
                errors.get().map(Object::toString).collect(Collectors.joining("\n-------------------------\n"))
                + "\n-------------------------",
                errors.get().map(e -> new StackTraceElement("?", "?", fileName, (int) e.line)).toArray(StackTraceElement[]::new))
        );
        return this;
    }

    public JavaCompileException initDetailsWithFileContent(String fileContent) {
        initCause(new JavaCompileException_DETAILS(
                "\n" +
                fileContent +
                "\n-------------------------\n" +
                errors.get().map(Object::toString).collect(Collectors.joining("\n-------------------------\n"))
                + "\n-------------------------",
                new StackTraceElement[0])
        );
        return this;
    }
}

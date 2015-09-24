package org.metaja.compiler;

class JavaCompileException_DETAILS extends Throwable {
    public JavaCompileException_DETAILS(String message, StackTraceElement[] elements) {
        super(message);
        setStackTrace(elements);
    }
}

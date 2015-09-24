package org.metaja.compiler;

public class JavaCompileError {
    public final long line;
    public final long column;
    public final String text;

    public JavaCompileError(long line, long column, String text) {
        this.line = line;
        this.column = column;
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("[line: %d, column: %d]: '%s'", line, column, text);
    }
}

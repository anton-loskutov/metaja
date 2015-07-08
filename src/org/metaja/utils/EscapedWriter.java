package org.metaja.utils;

import java.io.IOException;
import java.io.Writer;

public class EscapedWriter extends Writer {

    private final Writer out;

    public EscapedWriter(Writer out) {
        this.out = out;
    }

    @Override
    public void write(int c) throws IOException {
        append(c);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void write(char[] buf, int off, int len) throws IOException {
        for (int i = off, im = off + len; i < im; i++) {
            append(buf[i]);
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public EscapedWriter append(int c) throws IOException {
        if (c > 0xfff) {
            return out("\\u").out(hex(c));
        } else if (c > 0xff) {
            return out("\\u0").out(hex(c));
        } else if (c > 0x7f) {
            return out("\\u00").out(hex(c));
        } else if (c < 32) {
            switch (c) {
                case '\b':
                    return out("\\b");
                case '\n':
                    return out("\\n");
                case '\t':
                    return out("\\t");
                case '\f':
                    return out("\\f");
                case '\r':
                    return out("\\r");
                default:
                    if (c > 0xf) {
                        return out("\\u00").out(hex(c));
                    } else {
                        return out("\\u000").out(hex(c));
                    }
            }
        } else {
            switch (c) {
                case '\"':
                    return out("\\\"");
                case '\\':
                    return out("\\\\");
                default:
                    return out(c);
            }
        }
    }

    // ====== private =======

    private EscapedWriter out(String s) throws IOException {
        out.write(s);
        return this;
    }

    private EscapedWriter out(int c) throws IOException {
        out.write(c);
        return this;
    }

    private static String hex(int i) {
        return Integer.toHexString(i).toUpperCase();
    }
}

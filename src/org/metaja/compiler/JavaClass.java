package org.metaja.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.*;

public class JavaClass extends SimpleJavaFileObject {
    public final JavaClassName className;

    private byte[] bytes = null;

    public JavaClass(JavaClassName className) {
        super(className.toURI(), Kind.CLASS);
        this.className = className;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();
                JavaClass.this.bytes = buf;
                onByteCodeUpdated(JavaClass.this);
            }
        };
    }

    @Override
    public InputStream openInputStream() throws IOException {
        if (bytes == null) {
            throw new IllegalStateException();
        }
        return new ByteArrayInputStream(bytes);
    }

    public byte[] getByteCode() {
        return bytes;
    }

    // ===== protected =====

    protected void onByteCodeUpdated(JavaClass javaClass) {

    }
}

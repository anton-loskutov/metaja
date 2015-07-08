package org.metaja.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ResourceUtils {

    private ResourceUtils() {}

    public static class IORuntimeException extends RuntimeException {
        public IORuntimeException(IOException cause) {
            super(cause);
        }
    }

    public static byte[] readResource(ClassLoader cl, String path) throws IORuntimeException {
        try {
            InputStream stream = cl.getResourceAsStream(path);
            if (stream == null) {
                return null;
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int i = 0; i >= 0; i = stream.read(buffer, 0, buffer.length)) {
                bytes.write(buffer, 0, i);
            }
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public static String readResource(ClassLoader cl, String path, Charset cs) throws IORuntimeException {
        byte[] bytes = readResource(cl, path);
        if (bytes == null) {
            throw new IllegalArgumentException("Can not find '" + path + "' on classpath!");
        }
        return new String(bytes, cs);
    }

    public static byte[] readResource(String path) throws IORuntimeException {
        return readResource(ClassLoader.getSystemClassLoader(), path);
    }

    public static String readResource(String path, Charset cs) throws IORuntimeException {
        return readResource(ClassLoader.getSystemClassLoader(), path, cs);
    }
}

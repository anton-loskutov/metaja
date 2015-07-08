package org.metaja.compiler;

import java.net.URI;
import java.net.URISyntaxException;

public class JavaClassName {

    public final String simple;
    public final String pack;
    public final String full;

    public JavaClassName(String simple, String pack) {
        this.simple = validateSimpleClassName(simple);
        this.pack = validatePackageName(pack);
        this.full = pack.equals("") ? simple : pack + "." + simple;
    }

    public JavaClassName(String className) {
        int dot = className.lastIndexOf(".");
        if (dot >= 0) {
            this.simple = className.substring(dot);
            this.pack = className.substring(0, dot);
        } else {
            this.simple = className;
            this.pack = "";
        }
        this.full = className;
    }

    public URI toURI() {
        try {
            return new URI(full);
        } catch (URISyntaxException e) {
            throw new Error();
        }
    }

    public static String validateSimpleClassName(String simpleClassName) throws IllegalArgumentException {
        if (simpleClassName == null ) {
            throw new NullPointerException();
        }

        // todo: implement by JLS

        if (simpleClassName.contains(".")) {
            throw new IllegalArgumentException();
        }
        return simpleClassName;
    }

    public static String validatePackageName(String packageName) throws IllegalArgumentException {
        if (packageName == null ) {
            throw new NullPointerException();
        }

        // todo: implement by JLS

        return packageName;
    }
}

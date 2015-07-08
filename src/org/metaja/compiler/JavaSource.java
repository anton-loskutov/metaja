package org.metaja.compiler;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

public class JavaSource extends SimpleJavaFileObject {
    public final String contents;

    public JavaSource(String fileName, String contents) {
        super(URI.create("string:///" + fileName), Kind.SOURCE);
        this.contents = contents;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return contents;
    }
}

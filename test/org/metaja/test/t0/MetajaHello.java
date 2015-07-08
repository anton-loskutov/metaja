package org.metaja.test.t0;

import java.nio.charset.StandardCharsets;

public class MetajaHello {

    public static void main(String[] args) {
        ClassLoader defaultClassLoader = ClassLoader.getSystemClassLoader();

        // path of template on classpath
        String templatePath = NameHolderTemplate.class.getCanonicalName().replace('.', '/') + ".java";

        Class<NameHolder> c = org.metaja.Metaja.load(
            // class loader
            defaultClassLoader,

            // class name
            NameHolderTemplate.class.getCanonicalName(),

            // template file name (needed by javac)
            templatePath,

            // template content
            org.metaja.utils.ResourceUtils.readResource(defaultClassLoader, templatePath, StandardCharsets.UTF_8),

            // template argument value
            new Object[]{ "World" }
        );

        NameHolder instance = org.metaja.utils.ClassUtils.newInstance(c);

        System.out.println("Hello, " + instance.getName() + "!");
    }
}



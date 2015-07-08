package org.metaja.test.t0;

public class MetajaHelloSimplified {

    public static void main(String[] args) {

        NameHolder name = org.metaja.utils.ClassUtils.newInstance(org.metaja.Metaja.load(
                // class name
                NameHolderTemplate.class.getCanonicalName(),

                // template argument value
                new Object[]{ "World" }
        ));

        System.out.println("Hello, " + name.getName() + "!");
    }
}

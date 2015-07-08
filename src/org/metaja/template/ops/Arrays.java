package org.metaja.template.ops;

import java.util.List;

public class Arrays {

    Arrays(){}

    public static <T> List<T> asList(T[] array) {
        return java.util.Arrays.asList(array);
    }
}

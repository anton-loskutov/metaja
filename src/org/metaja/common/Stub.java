/*** (java.lang.Class iface) { ***/

package org.metaja.common;

import org.metaja.Metaja;
import org.metaja.utils.ClassUtils;

public class Stub /***# implements $iface.getCanonicalName()$ #***/ {

    private final Runnable stub;

    public Stub(Runnable stub) {
        this.stub = stub;
    }

    /***
     for (java.lang.reflect.Method method : iface.getMethods()) {
        # public void $method.getName()$( #
        COLLECTIONS.iterate(ARRAYS.asList(method.getParameterTypes()), new int[1], (type, c) -> { # $type.getCanonicalName()$ a$c[0]++$# }, () -> {#,#});
        # ) { #
        #     stub.run(); #
        # } #
     }
     ***/

     public static <T> T create(Class<T> iface, Runnable stub) {
         if (!iface.isInterface()) {
             throw new IllegalArgumentException();
         }
         return ClassUtils.newInstance(Metaja.<T>load(Stub.class.getName(), new Object[] { iface }), stub);
     }
}
/*** } ***/

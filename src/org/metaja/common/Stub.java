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
     REFLECTION.getMethods(iface, STATIC::notModified).forEach((method) -> {
        # public void $method.it().getName()$( #
        ITERATION.stream(method.it().getParameterTypes()).forEach((type) -> {
            # $type.it().getCanonicalName()$ a$type.ix()$#
            if (!type.last()) { #,# }
        });
        # ) { #
        #     stub.run(); #
        # } #
     });
     ***/

     public static <T> T create(Class<T> iface, Runnable stub) {
         if (!iface.isInterface()) {
             throw new IllegalArgumentException();
         }
         return ClassUtils.newInstance(Metaja.<T>load(Stub.class.getName(), new Object[] { iface }), stub);
     }
}
/*** } ***/

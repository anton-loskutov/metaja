/*** (java.lang.Class iface) { ***/

package org.metaja.common;

import org.metaja.Metaja;
import org.metaja.utils.ClassUtils;

import java.util.function.Consumer;

public class Composite implements Consumer/***#<$iface.getCanonicalName()$> , $iface.getCanonicalName()$ #***/ {

    private volatile /***$ iface.getCanonicalName() $//***/ Object delegate;

    public Composite(/***$ iface.getCanonicalName() $//***/ Object delegate) {
        this.delegate = delegate;
    }

    /***
    for (java.lang.reflect.Method method : iface.getMethods()) {
        # public void $method.getName()$( #
        COLLECTIONS.iterate(ARRAYS.asList(method.getParameterTypes()), new int[1], (type, c) -> { # $type.getCanonicalName()$ a$c[0]++$# }, () -> {#,#});
        # ) { #
        # delegate.$method.getName()$(#
        COLLECTIONS.iterate(ARRAYS.asList(method.getParameterTypes()), new int[1], (type, c) -> { #a$c[0]++$# }, () -> {#,#});
        #); } #
     }
     ***/

    @Override
    public void accept(/***$iface.getCanonicalName()$//***/Object delegate) {
        this.delegate = delegate;
    }

    public static <T> T create(Class<T> iface, T delegate) {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException();
        }
        return ClassUtils.newInstance(Metaja.<T>load(Composite.class.getName(), new Object[] { iface } ), delegate);
    }
}
/*** } ***/

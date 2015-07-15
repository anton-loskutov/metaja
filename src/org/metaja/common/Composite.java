/*** (java.lang.Class iface) { ***/

package org.metaja.common;

import org.metaja.Metaja;
import org.metaja.utils.ClassUtils;


public class Composite<T /***# extends $iface.getCanonicalName()$ #***/> implements IComposite<T>/***#, $iface.getCanonicalName()$ #***/ {

    private volatile T delegate;

    private Composite() {
    }

    @Override
    public T instance() {
        @SuppressWarnings("unchecked")
        T composite = (T) this;

        return composite;
    }

    @Override
    public void delegate(T delegate) {
        this.delegate = delegate;
    }

    /***
     REFLECTION.getMethods(iface, STATIC::notModified).forEach((method) -> {
        # public void $method.it().getName()$( #
        ITERATION.asStream(method.it().getParameterTypes()).forEach((type) -> {
            # $type.it().getCanonicalName()$ a$type.ix()$#
            if (!type.last()) { #,# }
        });
        # ) { #
        # delegate.$method.it().getName()$(#
        ITERATION.asStream(method.it().getParameterTypes()).forEach((type) -> {
            #a$type.ix()$#
            if (!type.last()) { #,# }
        });
        #); } #
     });
     ***/

    /***/
    public static <T> IComposite<T> create(Class<T> iface, T delegate) {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException();
        }

        @SuppressWarnings("unchecked")
        IComposite<T> instance = (IComposite<T>)ClassUtils.newInstance(true, Metaja.<T>load(Composite.class.getName(), new Object[]{iface}));

        instance.delegate(delegate);

        return instance;
    }
    /***/
}
/*** } ***/

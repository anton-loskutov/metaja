/*** (boolean x) { ***/

/*** String A_class = "org.metaja.test.t1.A"; ***/
/*** IMPORT(A_class.replace('.', '/') + ".java", "UTF-8", x); ***/

package org.metaja.test.t1;

public class B implements I {

    private final A a = /*** $ NEW(A_class) $ /***/ new A(); /***/

    public boolean get() {
        return a.get();
    }
}
/*** } ***/
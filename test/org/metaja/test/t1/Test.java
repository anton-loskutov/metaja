package org.metaja.test.t1;

import junit.framework.TestCase;
import org.metaja.Metaja;
import org.metaja.utils.ClassUtils;

public class Test extends TestCase {

    public void test() {
        assertTrue(load(true).get());
        assertTrue(!load(false).get());
    }

    private static I load(boolean value) {
        return ClassUtils.newInstance(Metaja.<I>load(B.class.getName(), new Object[]{value}));
    }
}

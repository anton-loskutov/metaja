package org.metaja.test.t2;

import junit.framework.TestCase;
import org.metaja.Metaja;
import org.metaja.utils.ClassUtils;

public class Test extends TestCase {

    public void testInt() throws ClassNotFoundException {

        IntArray intArray = ClassUtils.newInstance(Metaja.<IntArray>load(Array.class.getName(), new Object[]{"int"}), 1);

        assertEquals(intArray.put(0, 1), 0);
        assertEquals(intArray.get(0), 1);
    }

    public void testLong() {
        LongArray longArray = ClassUtils.newInstance(Metaja.<LongArray>load(Array.class.getName(), new Object[]{"long"}), 1);

        assertEquals(longArray.put(0, 1L), 0L);
        assertEquals(longArray.get(0), 1L);
    }

}

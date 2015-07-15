package org.metaja.test;

import junit.framework.TestCase;
import org.metaja.common.Composite;
import org.metaja.common.IComposite;
import org.metaja.common.Stub;

import java.util.Objects;

public class TestCommon extends TestCase {

    public interface I {
        void set(Integer i);
    }

    public class M implements I {
        public int i;

        @Override
        public void set(Integer i) {
            this.i = i;
        }
    }

    public void testComposite() {
        M m1 = new M();
        M m2 = new M();

        IComposite<I> i = Composite.create(I.class, m1);
        i.instance().set(1);

        assertEquals(1, m1.i);
        assertEquals(0, m2.i);

        i.delegate(m2);

        i.instance().set(2);

        assertEquals(1, m1.i);
        assertEquals(2, m2.i);
    }

    public void testStub() {
        String msg = "stub";
        I i = Stub.create(I.class, () -> {
            throw new RuntimeException(msg);
        });

        try {
            i.set(1);
            fail();
        } catch (RuntimeException ignore) {
            if (!Objects.equals(ignore.getMessage(), msg)) {
                fail();
            }
        }


    }

}

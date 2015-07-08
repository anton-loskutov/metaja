/*** (String type) { ***/

package org.metaja.test.t2;

public class Array
/*** if (type.equals("int"))  # implements IntArray #  ***/
/*** if (type.equals("long")) # implements LongArray # ***/
{
    private /***$ type $//***/ Object array[];

    /*** if(type.equals("int")) { ***/ int x; /*** } ***/

    public Array(int initialLength) {
        this.array = new /***$ type $//***/ Object [initialLength];
    }

    public int length() {
        return array.length;
    }

    public /***$ type $//***/ Object get(int index) {
        return array[index];
    }

    public /***$ type $//***/ Object put(int index, /***$ type $//***/ Object value) {
        /***$ type $//***/ Object oldValue = array[index];
        array[index] = value;
        return oldValue;
    }

    public void add(/***$ type $//***/ Object value) {
        int length = length();
        ensureCapacity(length + 1);
        array[length] = value;
    }

    private void ensureCapacity(int minCapacity) {
        if (array.length < minCapacity) {
            int oldCapacity = array.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity;

            /***$ type $//***/ Object newArray[] = new /***$ type $//***/ Object [newCapacity];
            System.arraycopy(array, 0, newArray, 0, oldCapacity);
            array = newArray;
        }
    }
}

/*** } ***/

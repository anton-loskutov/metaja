/*** (String name) { ***/

package org.metaja.test.t0;

public class NameHolderTemplate implements NameHolder {

    @Override
    public String getName() {
        /*** if (name != null) {
         # return "$ name $"; #
        } else {
         ***/
        return "Anonymous";
        /*** } ***/
    }
}

/*** } ***/
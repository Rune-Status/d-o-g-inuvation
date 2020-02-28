package org.rspeer.debugger.varp;

/**
 * @author Yasper
 * <p>
 * Encapsulates a lookup via the lookup button in the varp change panel.
 */
public final class VarpLookup extends VarpChange {

    public VarpLookup(int index, int value) {
        super(index, value, value, null);
    }

    @Override
    public String toString() {
        return String.format("Varp %d = %d", getIndex(), getNewValue());
    }
}

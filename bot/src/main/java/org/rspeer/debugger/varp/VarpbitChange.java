package org.rspeer.debugger.varp;

import org.rspeer.game.providers.RSVarpBit;

/**
 * @author Yasper
 * <p>
 * Encapsulates a change in a varpbit.
 */
public final class VarpbitChange {

    private final RSVarpBit bit;
    private final int previous;
    private final int current;

    public VarpbitChange(RSVarpBit bit, int previous, int current) {
        this.bit = bit;
        this.previous = previous;
        this.current = current;
    }

    public RSVarpBit getBit() {
        return bit;
    }

    public int getPrevious() {
        return previous;
    }

    public int getCurrent() {
        return current;
    }
}

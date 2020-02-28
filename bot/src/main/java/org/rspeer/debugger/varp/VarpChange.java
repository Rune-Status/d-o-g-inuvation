package org.rspeer.debugger.varp;

import java.time.Instant;

/**
 * @author Yasper
 * <p>
 * Record of a varp change with all the varpbit changes if present.
 */
public class VarpChange implements Comparable<VarpChange> {

    private final int index;
    private final int oldValue;
    private final int newValue;
    private final VarpbitChange[] changes;
    private final Instant time;

    public VarpChange(int index, int oldValue, int newValue, VarpbitChange[] changes) {
        this.index = index;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changes = changes;
        time = Instant.now();
    }

    public int getNewValue() {
        return newValue;
    }

    public int getOldValue() {
        return oldValue;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return String.format("Varp %d: %d -> %d", index, oldValue, newValue);
    }

    public VarpbitChange[] getChanges() {
        return changes;
    }

    @Override
    public int compareTo(VarpChange o) {
        int diff = -time.compareTo(o.time);
        return diff == 0 ? -Integer.compare(index, o.index) : diff;
    }
}

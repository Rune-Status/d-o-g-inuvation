package org.rspeer.game.api;

public final class OverheadIcon {

    public static final int TYPE_SKULL = 439;
    public static final int TYPE_PRAYER = 440;

    private final int index;
    private final int type;
    private final int mask;

    public OverheadIcon(int index, int type, int mask) {
        this.index = index;
        this.type = type;
        this.mask = mask;
    }

    public int getIndex() {
        return index;
    }

    public int getType() {
        return type;
    }

    public int getMask() {
        return mask;
    }
}

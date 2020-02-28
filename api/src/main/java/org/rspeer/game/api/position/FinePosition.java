package org.rspeer.game.api.position;

import org.rspeer.game.api.scene.Scene;

public final class FinePosition implements Position {

    private final int x, y;
    private final byte floorLevel;

    FinePosition(int x, int y, byte floorLevel) {
        this.x = x;
        this.y = y;
        this.floorLevel = floorLevel;
    }

    public String toString() {
        return "FinePosition[x=" + x + ", y=" + y + ", floor=" + floorLevel + "]";
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public byte getFloorLevel() {
        return floorLevel;
    }

    @Override
    public FinePosition translate(int x, int y) {
        return Position.absolute(this.x + x, this.y + y, floorLevel);
    }

    @Override
    public WorldPosition getPosition() {
        return Scene.getBase().translate(x >> 9, y >> 9);
    }

    @Override
    public FinePosition getAbsolutePosition() {
        return this;
    }

    @Override
    public ScenePosition getScenePosition() {
        return Position.regional(x >> 9, y >> 9);
    }

    public boolean equals(Object o) {
        FinePosition pos = null;
        if (o instanceof FinePosition) {
            pos = (FinePosition) o;
        } else if (o instanceof Position) {
            pos = ((Position) o).getAbsolutePosition();
        }
        return pos != null && pos.getX() == x && pos.getY() == y && pos.getFloorLevel() == floorLevel;
    }
}

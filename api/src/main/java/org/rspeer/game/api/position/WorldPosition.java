package org.rspeer.game.api.position;

import org.rspeer.game.api.scene.Scene;

public final class WorldPosition implements Position {

    private final int x, y;
    private final byte floorLevel;

    WorldPosition(int x, int y, byte floorLevel) {
        this.x = x;
        this.y = y;
        this.floorLevel = floorLevel;
    }

    public String toString() {
        return "WorldPosition[x=" + x + ", y=" + y + ", floor=" + floorLevel + "]";
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
    public WorldPosition translate(int x, int y) {
        return Position.global(this.x + x, this.y + y, floorLevel);
    }

    @Override
    public WorldPosition getPosition() {
        return this;
    }

    @Override
    public FinePosition getAbsolutePosition() {
        return getScenePosition().getAbsolutePosition();
    }

    @Override
    public ScenePosition getScenePosition() {
        WorldPosition base = Scene.getBase();
        return Position.regional(x - base.x, y - base.y, floorLevel);
    }

    public boolean equals(Object o) {
        WorldPosition pos = null;
        if (o instanceof WorldPosition) {
            pos = (WorldPosition) o;
        } else if (o instanceof Position) {
            pos = ((Position) o).getPosition();
        }
        return pos != null && pos.getX() == x && pos.getY() == y && pos.getFloorLevel() == floorLevel;
    }
}

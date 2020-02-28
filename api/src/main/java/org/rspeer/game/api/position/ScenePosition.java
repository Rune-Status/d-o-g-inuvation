package org.rspeer.game.api.position;

import org.rspeer.game.api.scene.Scene;

public final class ScenePosition implements Position {

    private final int x, y;
    private final byte floorLevel;

    ScenePosition(int x, int y, byte floorLevel) {
        this.x = x;
        this.y = y;
        this.floorLevel = floorLevel;
    }

    public String toString() {
        return "ScenePosition[x=" + x + ", y=" + y + ", floor=" + floorLevel + "]";
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
    public ScenePosition translate(int x, int y) {
        return Position.regional(this.x + x, this.y + y, floorLevel);
    }

    @Override
    public WorldPosition getPosition() {
        return Scene.getBase().translate(x, y);
    }

    @Override
    public FinePosition getAbsolutePosition() {
        return Position.absolute(x << 9, y << 9);
    }

    @Override
    public ScenePosition getScenePosition() {
        return this;
    }

    public boolean equals(Object o) {
        ScenePosition pos = null;
        if (o instanceof ScenePosition) {
            pos = (ScenePosition) o;
        } else if (o instanceof Position) {
            pos = ((Position) o).getScenePosition();
        }
        return pos != null && pos.getX() == x && pos.getY() == y && pos.getFloorLevel() == floorLevel;
    }
}

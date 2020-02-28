package org.rspeer.game.api.position;

import org.rspeer.game.api.scene.Players;
import org.rspeer.game.api.scene.Projection;
import org.rspeer.game.api.scene.Scene;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public interface Position extends Positionable {

    static WorldPosition global(int x, int y, int floorLevel) {
        return new WorldPosition(x, y, (byte) floorLevel);
    }

    static WorldPosition global(int x, int y) {
        return global(x, y, (byte) 0);
    }

    static ScenePosition regional(int x, int y, int floorLevel) {
        return new ScenePosition(x, y, (byte) floorLevel);
    }

    static ScenePosition regional(int x, int y) {
        return regional(x, y, (byte) 0);
    }

    static FinePosition absolute(int x, int y, int floorLevel) {
        return new FinePosition(x, y, (byte) floorLevel);
    }

    static FinePosition absolute(int x, int y) {
        return absolute(x, y, (byte) 0);
    }

    static WorldPosition local() {
        return Players.getLocal().getPosition();
    }

    static WorldPosition base() {
        return Scene.getBase();
    }

    int getX();

    int getY();

    byte getFloorLevel();

    Position translate(int x, int y);

    default double distance(Positionable pos) {
        return Distance.between(this, pos);
    }

    default double distance() {
        return distance(Players.getLocal());
    }

    default Point toViewport() {
        return Projection.toViewport(this);
    }

    default Polygon getShape() {
        return Projection.getTileShape(this);
    }

    default Point toMinimap() {
        return Projection.toMinimap(this);
    }

    default boolean isInScene() {
        Position pos = getScenePosition();
        return pos.getX() >= 0 && pos.getY() >= 0
                && pos.getX() < 104 && pos.getY() < 104;
    }

    default boolean isInRegion() {
        Scene.Region region = Scene.getCurrentRegion();
        return region != null && region.getArea().contains(this);
    }

    /**
     * Gets the neighbouring tiles from this position.
     *
     * @param diagonal <p>
     *                 used to indicate whether or not you want to return the diagonal tiles: {@link Direction#NORTH_EAST},
     *                 {@link Direction#NORTH_WEST}, {@link Direction#SOUTH_EAST} and {@link Direction#SOUTH_WEST}
     *                 </p>
     * @return the list of positions neighbouring this position
     */
    default List<Position> getNeighbors(boolean diagonal) {
        List<Position> positions = new ArrayList<>(diagonal ? 8 : 4);

        for (Direction direction : Direction.values()) {
            if (!diagonal && !direction.isCardinal()) {
                continue;
            }

            positions.add(direction.translate(this));
        }

        return positions;
    }

    /**
     * Calls {@link #getNeighbors(boolean)} with boolean diagonal false.
     *
     * @return the list of positions neighbouring this position without the diagonal tiles
     */
    default List<Position> getNeighbors() {
        return getNeighbors(false);
    }
}

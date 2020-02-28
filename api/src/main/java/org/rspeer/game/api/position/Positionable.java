package org.rspeer.game.api.position;

import org.rspeer.game.api.scene.Players;
import org.rspeer.game.providers.RSSceneEntity;

public interface Positionable {

    ScenePosition getScenePosition();

    WorldPosition getPosition();

    FinePosition getAbsolutePosition();

    default int getX() {
        return getPosition().getX();
    }

    default int getY() {
        return getPosition().getY();
    }

    default byte getFloorLevel() {
        return getPosition().getFloorLevel();
    }

    default double distance(Positionable other) {
        return getPosition().distance(other.getPosition());
    }

    default double distance() {
        return distance(Players.getLocal());
    }

    default Area getArea() {
        if (this instanceof RSSceneEntity) {
            RSSceneEntity e = (RSSceneEntity) this;
            Position start = Position.regional(e.getStartX(), e.getStartY());
            Position end = Position.regional(e.getEndX(), e.getEndY());
            return Area.rectangular(start.getPosition(), end.getPosition(), e.getFloorLevel());
        }
        return Area.singular(getPosition());
    }
}

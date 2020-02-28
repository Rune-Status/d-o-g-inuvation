package org.rspeer.game.api.position;

import org.rspeer.api.commons.Random;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.tree.WalkAction;
import org.rspeer.game.api.scene.Scene;

public final class Movement {

    private Movement() {
        throw new IllegalAccessError();
    }

    public static void walkTo(Positionable position) {
        walkTo(position.getScenePosition());
    }

    public static void walkTo(ScenePosition position) {
        int x = clamp(position.getX(), 0, 104);
        int y = clamp(position.getY(), 0, 104);
        ActionProcessor.submit(new WalkAction(x, y));
    }

    public static void walkToExperimental(Position pos) {
        walkTo(getFurthestInRegion(pos));
    }

    private static Position getFurthestInRegion(Position pos1) {
        WorldPosition p1 = pos1.getPosition();
        WorldPosition p2 = Position.local();
        for (int i = 0; i < 10000; i++) {
            ScenePosition center = centerPosition(p1, p2).getScenePosition();
            if (isInRegion(center)) {
                p2 = center.getPosition();
            } else if (isOutsideRegion(center)) {
                p1 = center.getPosition();
            } else {
                return center;
            }
        }
        return pos1;
    }

    private static boolean isOutsideRegion(ScenePosition position) {
        return position.getX() > 104 || position.getX() < 0 || position.getY() > 104 || position.getY() < 0;
    }

    private static boolean isInRegion(ScenePosition position) {
        return position.getX() > 0 && position.getX() < 104 && position.getY() > 0 && position.getY() < 104;
    }

    private static Position centerPosition(Position pos1, Position pos2) {
        WorldPosition p1 = pos1.getPosition(), p2 = pos2.getPosition();
        return Position.global((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2);
    }

    public static void walkToRandom(Positionable target, int bound) {
        walkTo(target.getScenePosition().translate(Random.nextInt(-bound, bound), Random.nextInt(-bound, bound)));
    }

    public static void walkToRandom(Positionable position) {
        walkToRandom(position, 3);
    }

    private static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static boolean isDestinationSet() {
        int x = Game.getClient().getDestinationX();
        int y = Game.getClient().getDestinationY();
        return x >= 0 && y >= 0;
    }

    public static Position getDestination() {
        int x = Game.getClient().getDestinationX();
        int y = Game.getClient().getDestinationY();
        if (x < 0 || y < 0) {
            return null;
        }
        return Position.regional(x, y, Scene.getLevel()).getPosition();
    }
}

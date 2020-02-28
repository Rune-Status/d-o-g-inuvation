package org.rspeer.game.api.position;

public interface DistanceEvaluator {

    double evaluate(int x1, int y1, int x2, int y2);

    default double evaluate(Positionable from, Positionable to) {
        Position p1 = from.getScenePosition();
        Position p2 = to.getScenePosition();
        return evaluate(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
}
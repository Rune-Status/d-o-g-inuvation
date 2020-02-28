package org.rspeer.game.api.position;

import org.rspeer.game.api.scene.Players;

@Deprecated
public class Path {

    private Position[] path;
    private int idx = 0;

    public Path(Position... path) {
        this.path = path;
    }

    public void start() {
        double closestDist = 133700D;
        int closestIdx = -1;
        int i = 0;
        for (Position p : path) {
            if (closestIdx == -1 || p.distance() < closestDist) {
                closestDist = p.distance();
                closestIdx = i;
            }
            i++;
        }
        idx = closestIdx;
    }

    private Position nearest() {
        double closestDist = 133700D;
        Position closest = null;
        for (Position p : path) {
            if (closest == null || p.distance() < closestDist) {
                closestDist = p.distance();
                closest = p;
            }
        }
        return closest;
    }

    public double distance() {
        return nearest().distance();
    }

    public void step() {
        if (path.length <= idx) return;
        Position current = path[idx];
        if (current.distance() < 5) {
            Movement.walkTo(current);
            idx++;
        } else if (!Players.getLocal().isMoving()) {
            Movement.walkTo(current);
        }
    }

    public void reset() {
        idx = 0;
    }
}

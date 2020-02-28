package org.rspeer.game.api.position.path;

import org.rspeer.game.api.position.Movement;
import org.rspeer.game.api.position.Position;

public final class PredefinedPath implements Path {

    private final Position[] positions;
    private Position current;

    private PredefinedPath(Position... positions) {
        this.positions = positions;
    }

    public static PredefinedPath build(Position... positions) {
        return new PredefinedPath(positions);
    }

    @Override
    public boolean step() {
        int nearIndex = -1;
        int lastDistance = Integer.MAX_VALUE;
        for (int i = 0; i < positions.length; i++) {
            Position position = positions[i];
            double dist = position.distance();
            if (dist < lastDistance) {
                lastDistance = (int) dist;
                nearIndex = i;
            }
        }

        Position furthest = null;
        if (nearIndex != positions.length - 1) {
            furthest = positions[nearIndex + 1];
            for (int i = nearIndex; i < positions.length; i++) {
                Position position = positions[i];
                if (position.isInScene()) { //TODO
                    if (position.distance() <= 5) {
                        continue;
                    }
                    furthest = position;
                }
            }
        }

        current = furthest;

        if (furthest == null) {
            return false;
        }
        Movement.walkToExperimental(furthest);
        return true;
    }

    public Position getCurrent() {
        return current;
    }
}

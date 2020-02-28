package org.rspeer.game.api.commons.predicate;

import org.rspeer.game.api.position.Position;
import org.rspeer.game.api.position.Positionable;

import java.util.function.Predicate;

public class PositionPredicate implements Predicate<Positionable> {

    private final Position[] positions;

    public PositionPredicate(Position... positions) {
        this.positions = positions;
    }

    @Override
    public boolean test(Positionable positionable) {
        for (Position position : positions) {
            if (positionable.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }
}

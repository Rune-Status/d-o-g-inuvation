package org.rspeer.game.api.commons.predicate;
import org.rspeer.game.api.position.Area;
import org.rspeer.game.api.position.Positionable;

import java.util.function.Predicate;

public class AreaPredicate implements Predicate<Positionable> {

    private final Area[] areas;

    public AreaPredicate(Area... areas) {
        this.areas = areas;
    }

    @Override
    public boolean test(Positionable positionable) {
        for (Area area : areas) {
            if (area.contains(positionable)) {
                return true;
            }
        }
        return false;
    }
}

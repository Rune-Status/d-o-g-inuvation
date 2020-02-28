package org.rspeer.game.api.commons.predicate;

import org.rspeer.api.commons.Identifiable;

import java.util.function.Predicate;

public class IdPredicate<I extends Identifiable> implements Predicate<I> {

    private final int[] ids;

    public IdPredicate(int... ids) {
        this.ids = ids;
    }

    @Override
    public boolean test(I i) {
        for (int id : ids) {
            if (id == i.getId()) {
                return true;
            }
        }
        return false;
    }
}
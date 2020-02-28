package org.rspeer.game.api.commons.predicate;

import org.rspeer.api.commons.Identifiable;

import java.util.function.Predicate;

public class NamePredicate<I extends Identifiable> implements Predicate<I> {

    private final String[] names;
    private final boolean contains;

    public NamePredicate(boolean contains, String... names) {
        this.contains = contains;
        this.names = names;
    }

    public NamePredicate(String... names) {
        this(false, names);
    }

    @Override
    public boolean test(I i) {
        if (i.getName() == null) {
            return false;
        }
        for (String name : names) {
            if (contains ? i.getName().toLowerCase().contains(name.toLowerCase()) : i.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}

package org.rspeer.game.api.commons.predicate;

import org.rspeer.game.api.action.Interactable;

import java.util.function.Predicate;

public class ActionPredicate<I extends Interactable> implements Predicate<I> {

    private final String[] actions;
    private final boolean contains;

    public ActionPredicate(boolean contains, String... actions) {
        this.contains = contains;
        this.actions = actions;
    }

    public ActionPredicate(String... actions) {
        this(false, actions);
    }

    @Override
    public boolean test(I i) {
        for (String action : actions) {
            if (contains ? i.containsAction(x -> x.toLowerCase().contains(action)) : i.containsAction(action)) {
                return true;
            }
        }
        return false;
    }
}

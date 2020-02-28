package org.rspeer.game.api.action;

import org.rspeer.game.api.action.tree.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface Interactable {

    static int getActionIndex(String[] actions, Predicate<String> matcher) {
        if (matcher == null || actions == null) {
            return -1;
        }
        for (int i = 0; i < actions.length; i++) {
            String action = actions[i];
            if (action != null && matcher.test(action)) {
                return i;
            }
        }
        return -1;
    }

    static int getActionIndex(String[] actions, String action) {
        return getActionIndex(actions, x -> x != null && x.equalsIgnoreCase(action));
    }

    static String[] getFilteredActions(String[] raw) {
        if (raw == null) {
            return new String[0];
        }

        List<String> filtered = new ArrayList<>();
        for (String action : raw) {
            if (action != null) {
                filtered.add(action);
            }
        }
        return filtered.toArray(new String[0]);
    }

    String[] getActions();

    String[] getRawActions();

    boolean interact(int opcode);

    Action actionOf(String action);

    boolean interact(String action);

    default boolean interact(Predicate<String> matcher) {
        if (matcher.test("")) {
            return interact("");
        }

        for (String action : getActions()) {
            if (action != null && matcher.test(action)) {
                return interact(action);
            }
        }
        return false;
    }

    default boolean containsAction(String action) {
        return containsAction(x -> x.equalsIgnoreCase(action));
    }

    default boolean containsAction(Predicate<String> action) {
        String[] actions = getActions();
        if (actions == null) return false;
        for (String e : actions) {
            if (e != null && action.test(e)) {
                return true;
            }
        }
        return false;
    }

    default boolean containsAction(Pattern... patterns) {
        return containsAction(x -> {
            for (Pattern pattern : patterns) {
                if (x.matches(pattern.pattern())) {
                    return true;
                }
            }
            return false;
        });
    }
}

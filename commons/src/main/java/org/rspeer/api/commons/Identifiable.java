package org.rspeer.api.commons;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface Identifiable {

    static <K extends Identifiable> Predicate<K> predicate(int... ids) {
        return identifiable -> {
            for (int value : ids) {
                if (value == identifiable.getId()) {
                    return true;
                }
            }
            return false;
        };
    }

    static <K extends Identifiable> Predicate<K> predicate(String... names) {
        return identifiable -> {
            if (identifiable.getName() != null) {
                for (String name : names) {
                    if (identifiable.getName().toLowerCase().equals(name.toLowerCase())) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        };
    }

    static <K extends Identifiable> Predicate<K> predicate(Pattern... patterns) {
        return identifiable -> {
            if (identifiable.getName() != null) {
                for (Pattern pattern : patterns) {
                    if (identifiable.getName().matches(pattern.pattern())) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        };
    }

    int getId();

    // by default this throws an exception unless overriden
    default String getName() {
        throw new UnsupportedOperationException();
    }
}

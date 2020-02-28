package org.rspeer.api.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Predicates {

    private static final Predicate ALWAYS = t -> true;
    private static final Predicate NEVER = ALWAYS.negate();

    private Predicates() {
        throw new IllegalAccessError();
    }

    public static <K> Predicate<K> always() {
        return ALWAYS;
    }

    public static <K> Predicate<K> never() {
        return NEVER;
    }

    public static <K> K firstMatching(Predicate<? super K> predicate, K... elems) {
        for (K elem : elems) {
            if (elem != null && predicate.test(elem)) {
                return elem;
            }
        }
        return null;
    }

    public static <K> boolean matching(Predicate<? super K> predicate, K... elems) {
        return firstMatching(predicate, elems) != null;
    }

    public static <K> K firstMatching(Predicate<? super K> predicate, Iterable<K> elems) {
        for (K elem : elems) {
            if (elem != null && predicate.test(elem)) {
                return elem;
            }
        }
        return null;
    }

    public static <K> boolean matching(Predicate<? super K> predicate, Iterable<K> elems) {
        return firstMatching(predicate, elems) != null;
    }

    public static <K> List<K> allMatching(Predicate<? super K> predicate, Iterable<K> elems) {
        List<K> match = new ArrayList<>();
        for (K elem : elems) {
            if (predicate.test(elem)) {
                match.add(elem);
            }
        }
        return match;
    }

    public static <K> List<K> allMatching(Predicate<? super K> predicate, K... elems) {
        List<K> match = new ArrayList<>();
        for (K elem : elems) {
            if (predicate.test(elem)) {
                match.add(elem);
            }
        }
        return match;
    }

    public static <K> Predicate<K> or(Predicate<K> a, Predicate<K> b) {
        return t -> a.test(t) || b.test(t);
    }

    public static <K> Predicate<K> and(Predicate<K> a, Predicate<K> b) {
        return t -> a.test(t) && b.test(t);
    }
}

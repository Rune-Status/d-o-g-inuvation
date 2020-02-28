package org.rspeer.game.api.position;

import org.rspeer.game.api.query.results.PositionableQueryResults;
import org.rspeer.game.api.scene.Players;

import java.util.*;
import java.util.function.Predicate;

public enum Distance implements DistanceEvaluator {

    EUCLIDEAN {
        @Override
        public double evaluate(int x1, int y1, int x2, int y2) {
            return Math.hypot(x2 - x1, y2 - y1);
        }
    },

    EUCLIDEAN_SQUARED {
        @Override
        public double evaluate(int x1, int y1, int x2, int y2) {
            return Math.sqrt(Math.pow(x2 - x1, 2)) + Math.sqrt(Math.pow(y2 - y1, 2));
        }
    },

    MANHATTAN {
        @Override
        public double evaluate(int x1, int y1, int x2, int y2) {
            return Math.abs(x2 - x1) + Math.abs(y2 - y1);
        }
    };

    private static final DistanceEvaluator DEFAULT_EVALUATOR = Distance.EUCLIDEAN_SQUARED;

    public static DistanceEvaluator getDefaultEvaluator() {
        return DEFAULT_EVALUATOR;
    }

    public static <K extends Positionable> K getNearest(K[] array, Positionable source, Predicate<K> predicate) {
        double distance = Integer.MAX_VALUE;
        K closest = null;
        for (K entity : array) {
            if (predicate.test(entity) && entity.getScenePosition().distance(source) < distance) {
                closest = entity;
                distance = entity.getScenePosition().distance(source);
            }
        }
        return closest;
    }

    public static <K extends Positionable> K getNearest(K[] array, Predicate<K> predicate) {
        return getNearest(array, Players.getLocal(), predicate);
    }

    public static <K extends Positionable> K getNearest(PositionableQueryResults<K> results, Positionable source, Predicate<K> predicate) {
        double distance = Integer.MAX_VALUE;
        K closest = null;
        for (K entity : results) {
            if (predicate.test(entity) && entity.getScenePosition().distance(source) < distance) {
                closest = entity;
                distance = entity.getScenePosition().distance(source);
            }
        }
        return closest;
    }

    public static <K extends Positionable> K getNearest(PositionableQueryResults<K> results, Predicate<K> predicate) {
        return getNearest(results, Players.getLocal(), predicate);
    }

    public static <K extends Positionable> K[] sort(K[] original, DistanceEvaluator evaluator, boolean ascending) {
        Arrays.sort(original, (K o1, K o2) -> (int) ((ascending ? 1 : -1) * evaluator.evaluate(o1, o2)));
        return original;
    }

    public static <K extends Positionable> K[] sort(K[] original, DistanceEvaluator evaluator) {
        return sort(original, evaluator, true);
    }

    public static <K extends Positionable> K[] sort(K[] original) {
        return sort(original, Distance.getDefaultEvaluator());
    }

    public static <K extends Positionable> List<K> sortFrom(Positionable positionable, Collection<K> locatables, DistanceEvaluator evaluator) {
        if (locatables == null) {
            return Collections.emptyList();
        }
        List<K> list = new ArrayList<>(locatables);
        if (positionable != null) {
            Position tile = positionable.getPosition();
            HashMap<Positionable, Double> hashMap = new HashMap<>(Math.max(locatables.size(), 16));
            for (Object locatable : locatables) {
                Positionable pos = (Positionable) locatable;
                hashMap.put(pos, Distance.evaluate(evaluator, tile, pos));
            }
            list.sort((one, two) -> {
                Double x1 = hashMap.get(one);
                Double x2 = hashMap.get(two);
                if (x1 != null && x2 != null) {
                    return Double.compare(x1, x2);
                } else {
                    throw new IllegalStateException("Value to compare must be non-null");
                }
            });
        }
        return list;
    }

    public static <K extends Positionable> List<K> sortFrom(Positionable positionable, Collection<K> locatables) {
        return sortFrom(positionable, locatables, Distance.getDefaultEvaluator());
    }

    /**
     * @param from The source
     * @param to   The destination
     * @return The distance between the 2 entities using the default evaluator
     */
    public static double between(Positionable from, Positionable to) {
        if (from == null || to == null) {
            return Double.MAX_VALUE;
        }
        return evaluate(DEFAULT_EVALUATOR, from, to);
    }

    /**
     * Evalues the distance to a positionable
     *
     * @param to The destination
     * @return The distance between the local player and the destination
     */
    public static double to(Positionable to) {
        if (to == null) {
            return Double.MAX_VALUE;
        }

        return evaluate(DEFAULT_EVALUATOR, Players.getLocal(), to);
    }

    public static double evaluate(DistanceEvaluator algo, Positionable from, Positionable to) {
        Position w1 = from.getScenePosition();
        Position w2 = to.getScenePosition();
        return algo.evaluate(w1.getX(), w1.getY(), w2.getX(), w2.getY());
    }

    public static double evaluate(DistanceEvaluator algo, int x1, int y1, int x2, int y2) {
        return algo.evaluate(x1, y1, x2, y2);
    }

    public abstract double evaluate(int x1, int y1, int x2, int y2);
}

package org.rspeer.game.api.query.results;

import org.rspeer.game.api.position.Distance;
import org.rspeer.game.api.position.DistanceEvaluator;
import org.rspeer.game.api.position.Positionable;
import org.rspeer.game.api.scene.Players;

import java.util.Collection;
import java.util.Comparator;

public final class PositionableQueryResults<K extends Positionable> extends QueryResults<K, PositionableQueryResults<K>> {

    public PositionableQueryResults(Collection<? extends K> results) {
        super(results);
    }

    public final PositionableQueryResults<K> sortByDistanceFrom(Positionable src, DistanceEvaluator eval) {
        return sort(Comparator.comparingDouble(value -> eval.evaluate(src, value)));
    }

    public final PositionableQueryResults<K> sortByDistanceFrom(Positionable src) {
        return sortByDistanceFrom(src, Distance.EUCLIDEAN_SQUARED);
    }

    public final PositionableQueryResults<K> sortByDistance(DistanceEvaluator eval) {
        return sortByDistanceFrom(Players.getLocal(), eval);
    }

    public final PositionableQueryResults<K> sortByDistance() {
        return sortByDistanceFrom(Players.getLocal());
    }

    public final K nearest() {
        return sortByDistance().first();
    }

    public final K furthest() {
        return sortByDistance().last();
    }

    public final K nearestTo(Positionable positionable) {
        return sortByDistanceFrom(positionable).first();
    }

    public final K furthestFrom(Positionable positionable) {
        return sortByDistanceFrom(positionable).last();
    }
}
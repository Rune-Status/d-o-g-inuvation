package org.rspeer.game.api.query;

import org.rspeer.game.api.position.Area;
import org.rspeer.game.api.position.Position;
import org.rspeer.game.api.position.Positionable;
import org.rspeer.game.api.scene.Scene;
import org.rspeer.game.api.commons.predicate.AreaPredicate;
import org.rspeer.game.api.commons.predicate.PositionPredicate;
import org.rspeer.game.api.query.results.PositionableQueryResults;

public abstract class PositionableQueryBuilder<K extends Positionable, Q extends QueryBuilder>
        extends QueryBuilder<K, Q, PositionableQueryResults<K>> {

    private Boolean reachable = null;

    private Integer distanceFromDefined = null;
    private Integer distanceFromLocal = null;

    private Positionable from = null;

    private Area[] areas = null;

    private Position[] positions = null;

    public Q on(Position... positions) {
        this.positions = positions;
        return self();
    }

    public Q reachable() {
        reachable = true;
        return self();
    }

    public Q unreachable() {
        reachable = false;
        return self();
    }

    public Q within(Positionable src, int distance) {
        from = src;
        this.distanceFromDefined = distance;
        return self();
    }

    public Q within(int distance) {
        distanceFromLocal = distance;
        return self();
    }

    public Q within(Area... areas) {
        this.areas = areas;
        return self();
    }

    @Override
    public boolean test(K entity) {
        if (distanceFromLocal != null && entity.distance() > distanceFromLocal) {
            return false;
        }

        if (distanceFromDefined != null && from != null && from.distance(entity) > distanceFromDefined) {
            return false;
        }

        if (positions != null && !new PositionPredicate(positions).test(entity)) {
            return false;
        }

        if (areas != null && !new AreaPredicate(areas).test(entity)) {
            return false;
        }

        if (reachable != null && reachable != Scene.getCollisionMap().isReachable(entity)) {
            return false;
        }

        return super.test(entity);
    }
}

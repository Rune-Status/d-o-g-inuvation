package org.rspeer.game.api.scene;

import org.rspeer.api.commons.Predicates;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.position.Distance;
import org.rspeer.game.api.query.results.PositionableQueryResults;
import org.rspeer.game.providers.RSAnimableObject;
import org.rspeer.game.providers.RSAnimableObjectNode;
import org.rspeer.game.providers.RSNodeTable;
import org.rspeer.game.api.commons.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Spot animation objects
 */
public final class DynamicObjects {

    private DynamicObjects() {
        throw new IllegalAccessError();
    }

    public static PositionableQueryResults<RSAnimableObject> getLoaded() {
        return getLoaded(Predicates.always());
    }

    public static PositionableQueryResults<RSAnimableObject> getLoaded(Predicate<RSAnimableObject> predicate) {
        RSNodeTable<RSAnimableObjectNode> nodes = Game.getClient().getAnimableObjectNodes();
        if (nodes == null) {
            return new PositionableQueryResults<>(Collections.emptyList());
        }

        List<RSAnimableObject> objects = new ArrayList<>();
        for (RSAnimableObjectNode node : nodes) {
            RSAnimableObject object = node.getAnimated();
            if (object != null && predicate.test(object)) {
                objects.add(object);
            }
        }
        return new PositionableQueryResults<>(objects);
    }

    public static RSAnimableObject getNearest(Predicate<RSAnimableObject> predicate) {
        return Distance.getNearest(getLoaded(Predicates.always()), predicate);
    }

    public static RSAnimableObject getNearest(int... ids) {
        return getNearest(x -> ArrayUtils.contains(ids, x.getId()));
    }
}

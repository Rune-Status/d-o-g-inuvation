package org.rspeer.game.api.scene;

import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.scene.GuidanceArrow;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.query.results.PositionableQueryResults;
import org.rspeer.game.providers.RSGuidanceArrow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Hint arrows
 */
public final class GuidanceArrows {

    private GuidanceArrows() {
        throw new IllegalAccessError();
    }

    public static PositionableQueryResults<GuidanceArrow> getLoaded(Predicate<GuidanceArrow> predicate) {
        List<GuidanceArrow> arrows = new ArrayList<>();
        for (RSGuidanceArrow node : Game.getClient().getGuidanceArrows()) {
            if (node != null && predicate.test(node.getAdapter())) {
                arrows.add(node.getAdapter());
            }
        }
        return new PositionableQueryResults<>(arrows);
    }

    public static PositionableQueryResults<GuidanceArrow> getLoaded() {
        return getLoaded(Predicates.always());
    }

    public static GuidanceArrow getFirst(Predicate<GuidanceArrow> predicate) {
        return getLoaded(predicate).first();
    }

    public static GuidanceArrow getNearest(Predicate<GuidanceArrow> predicate) {
        return getLoaded(predicate).nearest();
    }
}

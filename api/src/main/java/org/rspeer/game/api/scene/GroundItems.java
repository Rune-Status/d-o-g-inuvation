package org.rspeer.game.api.scene;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.adapter.node.NodeDeque;
import org.rspeer.game.adapter.scene.GroundItem;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.position.Distance;
import org.rspeer.game.api.query.results.PositionableQueryResults;
import org.rspeer.game.providers.RSGroundItem;
import org.rspeer.game.providers.RSGroundItemDeque;
import org.rspeer.game.providers.RSNodeTable;
import org.rspeer.game.api.query.GroundItemQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class GroundItems {

    private GroundItems() {
        throw new IllegalAccessError();
    }

    public static PositionableQueryResults<GroundItem> getLoaded(Predicate<GroundItem> predicate) {
        List<GroundItem> loaded = new ArrayList<>();
        RSNodeTable<RSGroundItemDeque> table = Game.getClient().getGroundItemDeques();
        if (table != null) {
            for (RSGroundItemDeque deque : table) {
                int x = ((int) deque.getHash() & 0x3FFF);
                int y = ((int) (deque.getHash() >> 14) & 0x3FFF);
                int plane = (int) ((deque.getHash() >> 28) & 0x3FFF);
                if (plane != Scene.getLevel()) {
                    continue;
                }
                for (RSGroundItem item : new NodeDeque<RSGroundItem>(deque.getDeque())) {
                    GroundItem gi = item.getAdapter();
                    gi.setPosition(x, y, plane);
                    if (predicate == null || predicate.test(gi)) {
                        loaded.add(gi);
                    }
                }
            }
        }
        return new PositionableQueryResults<>(loaded);
    }


    public static GroundItem getNearest(Predicate<GroundItem> predicate) {
        return Distance.getNearest(getLoaded(), predicate);
    }

    public static GroundItem getNearest(int... ids) {
        return getNearest(Identifiable.predicate(ids));
    }

    public static GroundItem getNearest(Pattern... patterns) {
        return getNearest(Identifiable.predicate(patterns));
    }

    public static GroundItem getNearest(String... names) {
        return getNearest(Identifiable.predicate(names));
    }

    public static PositionableQueryResults<GroundItem> getLoaded() {
        return getLoaded(e -> true);
    }

    public static GroundItemQueryBuilder newQuery() {
        return new GroundItemQueryBuilder();
    }
}

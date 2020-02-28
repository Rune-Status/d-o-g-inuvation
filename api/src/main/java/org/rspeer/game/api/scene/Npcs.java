package org.rspeer.game.api.scene;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.position.Distance;
import org.rspeer.game.api.query.results.PositionableQueryResults;
import org.rspeer.game.providers.RSNodeTable;
import org.rspeer.game.providers.RSNpc;
import org.rspeer.game.providers.RSObjectNode;
import org.rspeer.game.api.query.NpcQueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Npcs {

    private Npcs() {
        throw new IllegalAccessError();
    }

    public static PositionableQueryResults<Npc> getLoaded(Predicate<Npc> predicate) {
        RSNodeTable<RSObjectNode> table = Game.getClient().getNpcObjectNodeTable();
        if (table == null) {
            return new PositionableQueryResults<>(Collections.emptyList());
        }

        List<Npc> npcs = new ArrayList<>(table.getSize());
        for (RSObjectNode node : table) {
            if (node != null) {
                Object referent = node.getReferent();
                if (referent instanceof RSNpc) {
                    Npc npc = ((RSNpc) referent).getAdapter();
                    if (predicate.test(npc)) {
                        npcs.add(npc);
                    }
                }
            }
        }
        return new PositionableQueryResults<>(npcs);
    }

    public static PositionableQueryResults<Npc> getLoaded() {
        return getLoaded(e -> true);
    }

    public static Npc getNearest(Predicate<Npc> predicate) {
        return Distance.getNearest(getLoaded(), predicate);
    }

    public static Npc getNearest(String... names) {
        return getNearest(Identifiable.predicate(names));
    }

    public static Npc getNearest(Pattern... patterns) {
        return getNearest(Identifiable.predicate(patterns));
    }

    public static Npc getNearest(int... ids) {
        return getNearest(Identifiable.predicate(ids));
    }

    public static Npc getNearest() {
        return getNearest(e -> true);
    }

    public static NpcQueryBuilder newQuery() {
        return new NpcQueryBuilder();
    }
}

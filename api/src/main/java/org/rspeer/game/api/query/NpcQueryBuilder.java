package org.rspeer.game.api.query;

import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.api.scene.Npcs;
import org.rspeer.game.api.commons.predicate.ActionPredicate;
import org.rspeer.game.api.commons.predicate.IdPredicate;
import org.rspeer.game.api.query.results.PositionableQueryResults;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public final class NpcQueryBuilder extends MobileQueryBuilder<Npc, NpcQueryBuilder> {

    private int[] ids = null;

    private String[] actions = null;

    @Override
    public Supplier<List<? extends Npc>> getDefaultProvider() {
        return () -> Npcs.getLoaded().asList();
    }

    @Override
    protected PositionableQueryResults<Npc> createQueryResults(Collection<? extends Npc> raw) {
        return new PositionableQueryResults<>(raw);
    }

    public NpcQueryBuilder actions(String... actions) {
        this.actions = actions;
        return self();
    }

    public NpcQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    @Override
    public boolean test(Npc npc) {
        if (actions != null && !new ActionPredicate<>(actions).test(npc)) {
            return false;
        }

        if (ids != null && !new IdPredicate<>(ids).test(npc)) {
            return false;
        }

        return super.test(npc);
    }
}

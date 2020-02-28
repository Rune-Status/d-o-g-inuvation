package org.rspeer.game.api.query;

import org.rspeer.game.adapter.scene.GroundItem;
import org.rspeer.game.api.scene.GroundItems;
import org.rspeer.game.api.commons.Range;
import org.rspeer.game.api.commons.predicate.ActionPredicate;
import org.rspeer.game.api.commons.predicate.IdPredicate;
import org.rspeer.game.api.commons.predicate.NamePredicate;
import org.rspeer.game.api.query.results.PositionableQueryResults;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public final class GroundItemQueryBuilder extends PositionableQueryBuilder<GroundItem, GroundItemQueryBuilder> {

    private Boolean stackable = null;
    private Boolean noted = null;

    private Range amount = null;

    private int[] ids = null;

    private String[] names = null;
    private String[] nameContains = null;
    private String[] actions = null;

    @Override
    public Supplier<List<? extends GroundItem>> getDefaultProvider() {
        return () -> GroundItems.getLoaded().asList();
    }

    @Override
    protected PositionableQueryResults<GroundItem> createQueryResults(Collection<? extends GroundItem> raw) {
        return new PositionableQueryResults<>(raw);
    }

    public GroundItemQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public GroundItemQueryBuilder names(String... names) {
        this.names = names;
        return self();
    }

    public GroundItemQueryBuilder nameContains(String... names) {
        this.nameContains = names;
        return self();
    }

    public GroundItemQueryBuilder actions(String... actions) {
        this.actions = actions;
        return self();
    }

    public GroundItemQueryBuilder stackable() {
        stackable = true;
        return self();
    }

    public GroundItemQueryBuilder nonstackable() {
        stackable = false;
        return self();
    }

    public GroundItemQueryBuilder noted() {
        noted = true;
        return self();
    }

    public GroundItemQueryBuilder unnoted() {
        noted = false;
        return self();
    }

    public GroundItemQueryBuilder amount(int minInclusive) {
        return amount(minInclusive, Integer.MAX_VALUE);
    }

    public GroundItemQueryBuilder amount(int minInclusive, int maxInclusive) {
        amount = Range.of(minInclusive, maxInclusive);
        return self();
    }

    /*public GroundItemQueryBuilder on(GroundItem... positions) {
        return provider(() -> {
            List<GroundItem> pickables = new ArrayList<>();
            for (Position position : positions) {
                Collections.addAll(pickables, GroundItems.getAt(position));
            }
            return pickables;
        }).on(positions);
    }*/ //TODO

    @Override
    public boolean test(GroundItem item) {
        if (ids != null && !new IdPredicate<>(ids).test(item)) {
            return false;
        }

        if (names != null && !new NamePredicate<>(names).test(item)) {
            return false;
        }

        if (nameContains != null && !new NamePredicate<>(true, nameContains).test(item)) {
            return false;
        }

        if (stackable != null && stackable != item.getDefinition().isStackable()) {
            return false;
        }

        if (noted != null && noted != item.getDefinition().isNoted()) {
            return false;
        }

        if (amount != null && !amount.within(item.getQuantity())) {
            return false;
        }

        if (actions != null && !new ActionPredicate<>(actions).test(item)) {
            return false;
        }

        return super.test(item);
    }
}

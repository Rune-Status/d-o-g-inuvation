package org.rspeer.game.api.query;

import org.rspeer.game.api.component.Item;
import org.rspeer.game.api.commons.ArrayUtils;
import org.rspeer.game.api.commons.Range;
import org.rspeer.game.api.commons.predicate.ActionPredicate;
import org.rspeer.game.api.commons.predicate.IdPredicate;
import org.rspeer.game.api.commons.predicate.NamePredicate;
import org.rspeer.game.api.query.results.ItemQueryResults;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class ItemQueryBuilder extends QueryBuilder<Item, ItemQueryBuilder, ItemQueryResults> {

    private final Supplier<List<? extends Item>> supplier;


    private Boolean stackable = null;
    private Boolean noted = null;

    private Range stackSize = null;

    private int[] ids = null;
    private int[] slots = null;

    private String[] names = null;
    private String[] nameContains = null;
    private String[] actions = null;

    public ItemQueryBuilder(Supplier<List<? extends Item>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Supplier<List<? extends Item>> getDefaultProvider() {
        return supplier;
    }

    @Override
    protected ItemQueryResults createQueryResults(Collection<? extends Item> raw) {
        return new ItemQueryResults(raw);
    }

    public ItemQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public ItemQueryBuilder names(String... names) {
        this.names = names;
        return self();
    }

    public ItemQueryBuilder nameContains(String... names) {
        this.nameContains = names;
        return self();
    }

    public ItemQueryBuilder actions(String... actions) {
        this.actions = actions;
        return self();
    }

    public ItemQueryBuilder slots(int... slots) {
        this.slots = slots;
        return self();
    }

    public ItemQueryBuilder stackable() {
        stackable = true;
        return self();
    }

    public ItemQueryBuilder nonstackable() {
        stackable = false;
        return self();
    }

    public ItemQueryBuilder noted() {
        noted = true;
        return self();
    }

    public ItemQueryBuilder unnoted() {
        noted = false;
        return self();
    }

    /**
     * @deprecated
     */
    public ItemQueryBuilder amount(int minInclusive) {
        return stackSize(minInclusive, Integer.MAX_VALUE);
    }

    /**
     * @deprecated
     */
    public ItemQueryBuilder amount(int minInclusive, int maxInclusive) {
        return stackSize(minInclusive, maxInclusive);
    }

    public ItemQueryBuilder stackSize(int minInclusive) {
        return stackSize(minInclusive, Integer.MAX_VALUE);
    }

    public ItemQueryBuilder stackSize(int minInclusive, int maxInclusive) {
        stackSize = Range.of(minInclusive, maxInclusive);
        return self();
    }

    @Override
    public boolean test(Item item) {
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

        if (stackSize != null && !stackSize.within(item.getStackSize())) {
            return false;
        }

        if (slots != null && !ArrayUtils.contains(slots, item.getIndex())) {
            return false;
        }

        if (actions != null && !new ActionPredicate<>(actions).test(item)) {
            return false;
        }

        return super.test(item);
    }
}

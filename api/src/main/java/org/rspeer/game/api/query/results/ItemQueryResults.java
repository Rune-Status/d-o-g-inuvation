package org.rspeer.game.api.query.results;

import org.rspeer.game.api.component.Item;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ConcurrentMap;

public final class ItemQueryResults extends QueryResults<Item, ItemQueryResults> {

    public ItemQueryResults(Collection<? extends Item> results) {
        super(results);
    }

    public ItemQueryResults indexed() {
        return sort(Comparator.comparingInt(Item::getIndex));
    }

    public ItemQueryResults sortByQuantity(boolean ascending) {
        return sort((o1, o2) -> ascending ? Integer.compare(o1.getStackSize(), o2.getStackSize())
                : Integer.compare(o2.getStackSize(), o1.getStackSize()));
    }
}

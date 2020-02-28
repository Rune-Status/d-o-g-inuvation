package org.rspeer.game.api.component;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.ItemTables;
import org.rspeer.game.api.query.ItemQueryBuilder;
import org.rspeer.game.api.query.results.ItemQueryResults;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class AreaLoot {

    private static final int TABLE_KEY = ItemTables.AREA_LOOT;
    private static final int GROUP_INDEX = 1622;
    private static final int ITEM_COMPONENT_INDEX = 10;

    private static final InterfaceAddress ADDRESS = new InterfaceAddress(
            GROUP_INDEX, ITEM_COMPONENT_INDEX
    );

    private static final InterfaceAddress CLOSE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Close"))
    );

    private static final InterfaceAddress TAKE_ALL_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Select"))
    );

    private AreaLoot() {
        throw new IllegalAccessError();
    }

    public static ItemQueryResults getItems(Predicate<Item> predicate) {
        return ItemTables.getItems(TABLE_KEY, ADDRESS, predicate);
    }

    public static ItemQueryResults getItems() {
        return getItems(Predicates.always());
    }

    public static Item getFirst(Predicate<Item> predicate) {
        return ItemTables.getFirst(TABLE_KEY, ADDRESS, predicate);
    }

    public static Item getFirst(int... ids) {
        return getFirst(Identifiable.predicate(ids));
    }

    public static Item getFirst(String... names) {
        return getFirst(Identifiable.predicate(names));
    }

    public static Item getFirst(Pattern... patterns) {
        return getFirst(Identifiable.predicate(patterns));
    }

    public static boolean contains(Predicate<Item> predicate) {
        return getFirst(predicate) != null;
    }

    public static boolean contains(int... ids) {
        return ItemTables.contains(TABLE_KEY, ids);
    }

    public static boolean containsAll(int... ids) {
        return ItemTables.containsAll(TABLE_KEY, ids);
    }

    public static boolean contains(String... names) {
        return contains(Identifiable.predicate(names));
    }

    public static boolean containsAll(String... names) {
        for (String name : names) {
            if (!contains(name)) {
                return false;
            }
        }
        return true;
    }

    public static boolean contains(Pattern... patterns) {
        return contains(Identifiable.predicate(patterns));
    }

    public static boolean containsAll(Pattern... patterns) {
        for (Pattern pattern : patterns) {
            if (!contains(pattern)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOpen() {
        InterfaceComponent component = ADDRESS.resolve();
        return component != null && component.isVisible();
    }

    public static boolean takeAll() {
        InterfaceComponent component = TAKE_ALL_ADDRESS.resolve();
        return component != null && component.interact("Select");
    }

    public static boolean take(Predicate<Item> predicate) {
        Item item = getFirst(predicate);
        return item != null && item.interact(x -> x.startsWith("Take"));
    }

    public static boolean take(String... names) {
        return take(Identifiable.predicate(names));
    }

    public static boolean take(int... ids) {
        return take(Identifiable.predicate(ids));
    }

    public static boolean close() {
        InterfaceComponent component = CLOSE_ADDRESS.resolve();
        return component != null && component.interact("Close");
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> getItems().asList());
    }
}

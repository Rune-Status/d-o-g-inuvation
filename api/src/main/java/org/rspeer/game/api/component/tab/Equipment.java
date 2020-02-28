package org.rspeer.game.api.component.tab;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.api.ItemTables;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Item;
import org.rspeer.game.api.query.ItemQueryBuilder;
import org.rspeer.game.api.query.results.ItemQueryResults;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Equipment {

    private static final int TABLE_KEY = ItemTables.EQUIPMENT;
    private static final int GROUP_INDEX = InterfaceComposite.EQUIPMENT.getGroup();
    private static final int ITEM_CONTAINER_INDEX = 15;

    private static final InterfaceAddress ADDRESS = new InterfaceAddress(
            GROUP_INDEX, ITEM_CONTAINER_INDEX
    );

    private Equipment() {
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

    public static boolean isOccupied(Slot slot) {
        return getItemAt(slot) != null;
    }

    public static Item getItemAt(Slot slot) {
        return getFirst(x -> x.getIndex() == slot.index);
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> getItems().asList());
    }

    public enum Slot {

        MAIN_HAND(3),
        HELM(0),
        CAPE(1),
        NECK(2),
        CHEST(4),
        OFF_HAND(5),
        LEGS(7),
        HANDS(9),
        FEET(10),
        RING(12),
        QUIVER(13),
        AURA(14),
        POCKET(17),
        SIGIL(18);

        private final int index;

        Slot(int index) {
            this.index = index;
        }
    }
}

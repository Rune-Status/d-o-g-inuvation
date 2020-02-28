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

/**
 * A utility class for manipulating items in the Backpack
 */
public final class Backpack {

    private static final int TABLE_KEY = ItemTables.BACKPACK;
    private static final int CAPACITY = 28;
    private static final int GROUP_INDEX = InterfaceComposite.BACKPACK.getGroup();
    private static final InterfaceAddress ADDRESS = new InterfaceAddress(GROUP_INDEX, 7);

    private Backpack() {
        throw new IllegalAccessError();
    }

    /**
     * @param predicate The predicate used to select the elements
     * @return An array of items in the backpack
     */
    public static ItemQueryResults getItems(Predicate<Item> predicate) {
        return ItemTables.getItems(TABLE_KEY, ADDRESS, predicate);
    }

    /**
     * @param patterns The patterns used to select the elements
     * @return An array of items in the backpack
     */
    public static ItemQueryResults getItems(Pattern... patterns) {
        return getItems(Identifiable.predicate(patterns));
    }

    /**
     * @param ids The ids used to select the elements
     * @return An array of items in the backpack
     */
    public static ItemQueryResults getItems(int... ids) {
        return getItems(Identifiable.predicate(ids));
    }

    /**
     * @param names The names used to select the elements
     * @return An array of items in the backpack
     */
    public static ItemQueryResults getItems(String... names) {
        return getItems(Identifiable.predicate(names));
    }

    /**
     * @return An array of items in the backpack
     */
    public static ItemQueryResults getItems() {
        return getItems(Predicates.always());
    }

    /**
     * @param predicate The ids used to select the elements
     * @return An first item in the backpack matching the parameter
     */
    public static Item getFirst(Predicate<Item> predicate) {
        return ItemTables.getFirst(TABLE_KEY, ADDRESS, predicate);
    }

    /**
     * @param ids The ids used to select the elements
     * @return An first item in the backpack matching the parameter
     */
    public static Item getFirst(int... ids) {
        return getFirst(Identifiable.predicate(ids));
    }

    /**
     * @param names The names used to select the elements
     * @return An first item in the backpack matching the parameter
     */
    public static Item getFirst(String... names) {
        return getFirst(Identifiable.predicate(names));
    }

    /**
     * @param patterns The patterns used to select the elements
     * @return An first item in the backpack matching the parameter
     */
    public static Item getFirst(Pattern... patterns) {
        return getFirst(Identifiable.predicate(patterns));
    }

    /**
     * @param predicate The predicate used to select the elements
     * @return {@code true} if the backpack contains an item matching the parameter
     */
    public static boolean contains(Predicate<Item> predicate) {
        return getFirst(predicate) != null;
    }

    /**
     * @param includeStacks true to count item stacks, else false
     * @param predicate     The predicate used to select the elements
     * @return The number of items in the backpack matching the parameter
     */
    public static int getCount(boolean includeStacks, Predicate<Item> predicate) {
        int count = 0;
        for (Item item : getItems(predicate)) {
            count += includeStacks ? item.getStackSize() : 1;
        }
        return count;
    }

    /**
     * @param predicate The predicate used to select the elements
     * @return The number of items in the backpack matching the parameter
     */
    public static int getCount(Predicate<Item> predicate) {
        return getCount(false, predicate);
    }

    /**
     * @param ids The ids used to select the elements
     * @return {@code true} if the backpack contains items matching any of the given ids
     */
    public static boolean contains(int... ids) {
        return ItemTables.contains(TABLE_KEY, ids);
    }

    /**
     * @param ids The names used to select the elements
     * @return {@code true} if the backpack contains items matching all of the given names
     */
    public static boolean containsAll(int... ids) {
        return ItemTables.containsAll(TABLE_KEY, ids);
    }

    /**
     * @param includeStacks true to count item stacks, else false
     * @param ids           The ids used to select the elements
     * @return The number of items in the backpack matching the parameter
     */
    public static int getCount(boolean includeStacks, int... ids) {
        return ItemTables.getCount(TABLE_KEY, includeStacks, ids);
    }

    /**
     * @param ids The ids used to select the elements
     * @return The number of items in the backpack matching the parameter
     */
    public static int getCount(int... ids) {
        return getCount(false, ids);
    }

    /**
     * @param names The names used to select the elements
     * @return {@code true} if the backpack contains items matching any of the given names
     */
    public static boolean contains(String... names) {
        return contains(Identifiable.predicate(names));
    }

    /**
     * @param names The names used to select the elements
     * @return {@code true} if the backpack contains items matching all of the given names
     */
    public static boolean containsAll(String... names) {
        for (String name : names) {
            if (!contains(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param includeStacks true to count item stacks, else false
     * @param names         The names used to select the elements
     * @return The number of items in the backpack matching the parameter
     */
    public static int getCount(boolean includeStacks, String... names) {
        return getCount(includeStacks, Identifiable.predicate(names));
    }

    /**
     * @param names The names used to select the elements
     * @return The number of items in the backpack matching the parameter
     */
    public static int getCount(String... names) {
        return getCount(false, names);
    }

    /**
     * @param includeStacks true to count item stacks, else false
     * @param patterns      The patterns used to select the elements
     * @return The number of items in the backpack matching the parameter
     */
    public static int getCount(boolean includeStacks, Pattern... patterns) {
        return getCount(includeStacks, Identifiable.predicate(patterns));
    }

    /**
     * @param patterns The patterns used to select the elements
     * @return The number of items in the backpack matching the parameter
     */
    public static int getCount(Pattern... patterns) {
        return getCount(false, patterns);
    }

    /**
     * @return The number of items in the backpack
     */
    public static int getCount() {
        return getCount(Predicates.always());
    }

    /**
     * @param patterns The patterns used to select the elements
     * @return {@code true} if the backpack contains items matching any of the given patterns
     */
    public static boolean contains(Pattern... patterns) {
        return contains(Identifiable.predicate(patterns));
    }

    /**
     * @param patterns The patterns used to select the elements
     * @return {@code true} if the backpack contains items matching all of the given patterns
     */
    public static boolean containsAll(Pattern... patterns) {
        for (Pattern pattern : patterns) {
            if (!contains(pattern)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return {@code true} if the backpack is empty
     */
    public static boolean isEmpty() {
        return getCount() == 0;
    }

    /**
     * @return {@code true} if the backpack is full
     */
    public static boolean isFull() {
        return getCount() == CAPACITY;
    }

    /**
     * @return The number of free slots in the backpack
     */
    public static int getEmptySlots() {
        return CAPACITY - getCount();
    }

    /**
     * @return the {@link Item} at the given index
     */
    public static Item getItemAt(int index) {
        return getFirst(x -> x.getIndex() == index);
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> getItems().asList());
    }
}

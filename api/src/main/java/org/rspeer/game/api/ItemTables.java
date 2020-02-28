package org.rspeer.game.api;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.Item;
import org.rspeer.game.api.query.results.ItemQueryResults;
import org.rspeer.game.providers.RSItemTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class ItemTables {

    public static final int BACKPACK = 93;
    public static final int EQUIPMENT = 94;
    public static final int BANK = 95;
    public static final int FAMILIAR = 530;
    public static final int AREA_LOOT = 773;
    public static final int MONEY_POUCH = 623;
    public static final int TRADE = 90;

    private ItemTables() {
        throw new IllegalAccessError();
    }

    public static RSItemTable lookup(int key) {
        return Functions.mapOrNull(() -> Game.getClient().getItemTables(), t -> t.getSynthetic(key));
    }

    public static ItemQueryResults getItems(int key, InterfaceAddress address, Predicate<Item> predicate) {
        List<Item> items = new ArrayList<>();

        RSItemTable table = lookup(key);
        if (table == null) {
            return new ItemQueryResults(items);
        }

        int[] stacks = table.getQuantities();
        int[] ids = table.getIds();
        if (ids == null || stacks == null) {
            return new ItemQueryResults(items);
        }

        for (int i = 0; i < stacks.length; i++) {
            if (ids[i] != -1) {
                Item item = new Item(i, ids[i], stacks[i], address);
                if (predicate.test(item)) {
                    items.add(item);
                }
            }
        }
        return new ItemQueryResults(items);
    }

    public static Item getFirst(int key, InterfaceAddress address, Predicate<Item> predicate) {
        RSItemTable table = lookup(key);
        if (table == null) {
            return null;
        }
        int[] stacks = table.getQuantities();
        int[] ids = table.getIds();
        if (ids == null || stacks == null) {
            return null;
        }

        for (int i = 0; i < stacks.length; i++) {
            if (ids[i] != -1) {
                Item item = new Item(i, ids[i], stacks[i], address);
                if (predicate.test(item)) {
                    return item;
                }
            }
        }
        return null;
    }

    public static boolean contains(int key, Predicate<Item> predicate) {
        return getFirst(key, null, predicate) != null;
    }

    public static int getCount(int key, boolean includeStacks, Predicate<Item> predicate) {
        int count = 0;
        for (Item item : getItems(key, null, predicate)) {
            count += includeStacks ? item.getStackSize() : 1;//:D:D
        }
        return count;
    }

    public static int getCount(int key, Predicate<Item> predicate) {
        return getCount(key, false, predicate);
    }

    public static boolean contains(int key, int... itemIds) {
        return Functions.mapOrElse(() -> lookup(key), table -> table.contains(itemIds));
    }

    public static boolean containsAll(int key, int... itemIds) {
        return Functions.mapOrElse(() -> lookup(key), table -> table.containsAll(itemIds));
    }

    public static int getCount(int key, boolean includeStacks, int... itemIds) {
        return Functions.mapOrDefault(() -> lookup(key), table -> table.getCount(includeStacks, itemIds), 0);
    }

    public static int getCount(int key, int... itemIds) {
        return getCount(key, false, itemIds);
    }

    public static boolean contains(int key, String... names) {
        return contains(key, Identifiable.predicate(names));
    }

    public static boolean containsAll(int key, String... names) {
        for (String name : names) {
            if (!contains(key, name)) {
                return false;
            }
        }
        return true;
    }

    public static int getCount(int key, boolean includeStacks, String... names) {
        return getCount(key, includeStacks, Identifiable.predicate(names));
    }

    public static int getCount(int key, String... names) {
        return getCount(key, false, names);
    }

    public interface Shop {

        int BURTHORPE_FREE = 657;
        int BURTHOPE_SHOP = 656;

        int FALADOR_GARDEN_CENTRE = 397;

        int TAVERLY_SUMMONING_SHOP = 628;
        int TAVERLY_SUMMONING_SHOP_FREE = 629;
    }
}

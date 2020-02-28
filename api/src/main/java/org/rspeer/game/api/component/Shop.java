package org.rspeer.game.api.component;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.api.ItemTables;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.query.results.ItemQueryResults;
import org.rspeer.game.api.scene.Npcs;
import org.rspeer.game.api.scene.SceneObjects;

import java.util.function.Predicate;
import java.util.regex.Pattern;

//TODO sell methods lol
public final class Shop {

    //    private static final int BUY_TABLE_KEY = ItemTables.SHOP_BUY; //TODO apparently each shop has different keys, be careful
    // Item container index and free component are the groups that contain interfaces with acitons
    private static final int GROUP_INDEX = InterfaceComposite.SHOP.getGroup();
    private static final int ITEM_CONTAINER_INDEX = 20; // 33

    private static final InterfaceAddress ADDRESS = new InterfaceAddress(
            GROUP_INDEX, ITEM_CONTAINER_INDEX
    );
    private static final InterfaceAddress FREE_ADDRESS = new InterfaceAddress(
            GROUP_INDEX, 14 // 34
    );

    private static final InterfaceAddress SELL_ITEM_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Sell 1"))
    );

    private Shop() {
        throw new IllegalAccessError();
    }

    public static ItemQueryResults getItems(int key, Predicate<Item> predicate) {
        return ItemTables.getItems(key, ADDRESS, predicate);
    }

    public static ItemQueryResults getItems(int key) {
        return getItems(key, Predicates.always());
    }

    public static Item getFirst(int key, Predicate<Item> predicate) {
        return ItemTables.getFirst(key, ADDRESS, predicate);
    }

    public static Item getFirst(int key, int... ids) {
        return getFirst(key, Identifiable.predicate(ids));
    }

    public static Item getFirst(int key, String... names) {
        return getFirst(key, Identifiable.predicate(names));
    }

    public static Item getFirst(int key, Pattern... patterns) {
        return getFirst(key, Identifiable.predicate(patterns));
    }

    public static ItemQueryResults getFreeItems(int key, Predicate<Item> predicate) {
        return ItemTables.getItems(key, FREE_ADDRESS, predicate);
    }

    public static ItemQueryResults getFreeItems(int key) {
        return getFreeItems(key, Predicates.always());
    }

    public static Item getFirstFree(int key, Predicate<Item> predicate) {
        return ItemTables.getFirst(key, FREE_ADDRESS, predicate);
    }

    public static Item getFirstFree(int key, int... ids) {
        return getFirstFree(key, Identifiable.predicate(ids));
    }

    public static Item getFirstFree(int key, String... names) {
        return getFirstFree(key, Identifiable.predicate(names));
    }

    public static Item getFirstFree(int key, Pattern... patterns) {
        return getFirstFree(key, Identifiable.predicate(patterns));
    }

    public static boolean contains(int key, Predicate<Item> predicate) {
        return getFirst(key, predicate) != null;
    }

    public static boolean contains(int key, int... ids) {
        return ItemTables.contains(key, ids);
    }

    public static boolean containsAll(int key, int... ids) {
        return ItemTables.containsAll(key, ids);
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

    public static boolean contains(int key, Pattern... patterns) {
        return contains(key, Identifiable.predicate(patterns));
    }

    public static boolean containsAll(int key, Pattern... patterns) {
        for (Pattern pattern : patterns) {
            if (!contains(key, pattern)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isOpen() {
        return Interfaces.isVisible(GROUP_INDEX, 0);
    }

    public static boolean open(Predicate<String> namePredicate) {
        Interactable shop;
        Npc npc = Npcs.getNearest(a -> (a.containsAction("Trade") || a.containsAction("Open-shop"))
                && namePredicate.test(a.getName()));
        if (npc != null) {
            shop = npc;
        } else {
            shop = SceneObjects.getNearest(a -> a.containsAction("Trade")
                    && namePredicate.test(a.getName())); // Do these even exist??
        }
        return shop != null && shop.interact(x -> x.contains("Trade") || x.contains("Open-shop"))
                && Time.sleepUntil(Shop::isOpen, 3000);
    }

    public static boolean open(String shopName) {
        return open(a -> a.equals(shopName));
    }

    public static boolean open() {
        return open(Predicates.always());
    }


    public static boolean takeFree(int key, Predicate<Item> predicate) {
        Item item = getFirstFree(key, predicate);
        return item != null && item.interact("Take 1");
    }

    public static boolean takeFree(int key, String... names) {
        return takeFree(key, Identifiable.predicate(names));
    }

    public static boolean takeFree(int key, int... ids) {
        return takeFree(key, Identifiable.predicate(ids));
    }

    public static boolean buy(int key, Predicate<Item> predicate) {
        Item item = getFirst(key, predicate);
        return item != null && item.interact("Buy 1");
    }

    public static boolean buy(int key, String... names) {
        return buy(key, Identifiable.predicate(names));
    }

    public static boolean buy(int key, int... ids) {
        return buy(key, Identifiable.predicate(ids));
    }

    public static boolean buyAll(int key, Predicate<Item> predicate) {
        Item item = getFirst(key, predicate);
        return item != null && item.interact("Buy All");
    }

    public static boolean buyAll(int key, String... names) {
        return buyAll(key, Identifiable.predicate(names));
    }

    public static boolean buyAll(int key, int... ids) {
        return buyAll(key, Identifiable.predicate(ids));
    }

    public static boolean isSellTabOpen() {
        InterfaceComponent component = SELL_ITEM_ADDRESS.resolve();
        return component != null && component.isVisible();
    }

    public static boolean openSellTab() {
        InterfaceComponent[] components = Interfaces.getComponents(
                a -> a.containsAction("Select") && a.getWidth() == 90);
        return components.length > 1 && components[1].interact("Select");
    }

    public static boolean sell(int key, Predicate<Item> predicate) {
        if (!isSellTabOpen() && !openSellTab()) {
            return false;
        }
        Item item = getFirst(key, predicate);
        return item != null && item.interact("Sell 1");
    }

    public static boolean sell(int key, String item) {
        return sell(key, a -> a.getName().equals(item));
    }

    public static boolean sellAll(int key, Predicate<Item> predicate) {
        Item item = getFirst(key, predicate);
        return item != null && item.interact("Sell All");
    }

    public static boolean sellAll(int key, String item) {
        return sellAll(key, a -> a.getName().equals(item));
    }
}

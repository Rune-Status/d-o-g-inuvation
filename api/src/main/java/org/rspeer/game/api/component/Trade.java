package org.rspeer.game.api.component;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.api.ItemTables;
import org.rspeer.game.api.query.results.ItemQueryResults;
import org.rspeer.game.api.scene.Players;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Trade {

    private static final int CAPACITY = 28;

    private static final int TABLE_KEY = ItemTables.TRADE;

    private static final int INITIAL_TRADE_GROUP = InterfaceComposite.INITIAL_TRADE_SCREEN.getGroup();
    private static final int CONFIRM_TRADE_GROUP = InterfaceComposite.CONFIRM_TRADE_SCREEN.getGroup();
    private static final int ITEM_TRADE_GROUP = InterfaceComposite.TRADE_ITEMS.getGroup();

    private static final List<Integer> TRADE_GROUPS = Arrays.asList(INITIAL_TRADE_GROUP, CONFIRM_TRADE_GROUP);

    private static final InterfaceAddress ITEM_ADDRESS = new InterfaceAddress(INITIAL_TRADE_GROUP, 17);

    private static final InterfaceAddress OFFER_INVENTORY_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(INITIAL_TRADE_GROUP, a -> a.containsAction("Offer All"))
    );

    private static final InterfaceAddress OFFER_COINS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(INITIAL_TRADE_GROUP, a -> a.containsAction("Add from pouch"))
    );

    private static final InterfaceAddress CONFIRM_BUTTON_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(CONFIRM_TRADE_GROUP, a -> a.containsAction("Accept"))
    );

    private static final InterfaceAddress DECLINE_BUTTON_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(CONFIRM_TRADE_GROUP, a -> a.containsAction("Decline"))
    );

    private Trade() {
        throw new IllegalAccessError();
    }

    public static ItemQueryResults getItems(Predicate<Item> predicate) {
        return ItemTables.getItems(TABLE_KEY, ITEM_ADDRESS, predicate);
    }

    public static ItemQueryResults getItems(Pattern... patterns) {
        return getItems(Identifiable.predicate(patterns));
    }

    public static ItemQueryResults getItems(int... ids) {
        return getItems(Identifiable.predicate(ids));
    }

    public static ItemQueryResults getItems(String... names) {
        return getItems(Identifiable.predicate(names));
    }

    public static ItemQueryResults getItems() {
        return getItems(Predicates.always());
    }

    public static Item getFirst(Predicate<Item> predicate) {
        return ItemTables.getFirst(TABLE_KEY, ITEM_ADDRESS, predicate);
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

    public static int getCount(boolean includeStacks, Predicate<Item> predicate) {
        int count = 0;
        for (Item item : getItems(predicate)) {
            count += includeStacks ? item.getStackSize() : 1;
        }
        return count;
    }

    public static int getCount(Predicate<Item> predicate) {
        return getCount(false, predicate);
    }

    public static boolean contains(int... ids) {
        return ItemTables.contains(TABLE_KEY, ids);
    }

    public static boolean containsAll(int... ids) {
        return ItemTables.containsAll(TABLE_KEY, ids);
    }

    public static int getCount(boolean includeStacks, int... ids) {
        return ItemTables.getCount(TABLE_KEY, includeStacks, ids);
    }

    public static int getCount(int... ids) {
        return getCount(false, ids);
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

    public static int getCount(boolean includeStacks, String... names) {
        return getCount(includeStacks, Identifiable.predicate(names));
    }

    public static int getCount(String... names) {
        return getCount(false, names);
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

    public static int getCount(boolean includeStacks, Pattern... patterns) {
        return getCount(includeStacks, Identifiable.predicate(patterns));
    }

    public static int getCount(Pattern... patterns) {
        return getCount(false, patterns);
    }

    public static int getCount() {
        return getCount(Predicates.always());
    }

    public static boolean isEmpty() {
        return getCount() == 0;
    }

    public static boolean isFull() {
        return getCount() == CAPACITY;
    }

    public static int getEmptySlots() {
        return CAPACITY - getCount();
    }

    public static Item getItemAt(int index) {
        return getFirst(x -> x.getIndex() == index);
    }

    public static boolean accept() {
        InterfaceComponent component = CONFIRM_BUTTON_ADDRESS.resolve();
        if (component == null || !component.isVisible()) {
            InterfaceComponent[] components = Interfaces.getComponents(INITIAL_TRADE_GROUP, a -> a.containsAction("Select"));
            if (components.length > 0) {
                component = components[0];
            }
        }
        return component != null && component.interact(a -> a.equals("Select") || a.equals("Accept"));
    }

    public static boolean decline() {
        InterfaceComponent component = DECLINE_BUTTON_ADDRESS.resolve();
        if (component == null || !component.isVisible()) {
            InterfaceComponent[] components = Interfaces.getComponents(INITIAL_TRADE_GROUP, a -> a.containsAction("Select"));
            if (components.length > 1) {
                component = components[1];
            }
        }
        return component != null && component.interact(a -> a.equals("Select") || a.equals("Decline"));
    }

    public static boolean isAccepted() {
        InterfaceComponent component = Interfaces.getFirst(a -> TRADE_GROUPS.contains(a.getGroupIndex()) && a.getText().contains("Waiting for other"));
        return component != null && component.isVisible();
    }

    public static boolean hasTraderAccepted() {
        InterfaceComponent component = Interfaces.getFirst(a -> TRADE_GROUPS.contains(a.getGroupIndex()) && a.getText().contains("Other player has"));
        return component != null && component.isVisible();
    }

    public static boolean offerInventory() {
        InterfaceComponent component = OFFER_INVENTORY_ADDRESS.resolve();
        return component != null && component.interact("Offer All");
    }

    public static InterfaceComponent[] getInventoryItems(Predicate<String> predicate) {
        InterfaceComponent component = Interfaces.getComponent(ITEM_TRADE_GROUP, 0);
        return component == null ? new InterfaceComponent[0] : component.getComponents(a -> a.getItemId() != -1 && predicate.test(a.getName()));
    }

    public static InterfaceComponent[] getInventoryItems(String... items) {
        return getInventoryItems(a -> Arrays.asList(items).contains(a));
    }

    public static InterfaceComponent[] getInventoryItems() {
        return getInventoryItems(a -> true);
    }

    public static void offer(int amount, String... items) {

        for (InterfaceComponent item : getInventoryItems(items)) {
            if (item == null) {
                continue;
            }
            if (amount == 1 && item.interact("Offer")) {
                break;
            } else if (amount > 27) {
                item.interact("Offer-All");
            } else if (item.interact("Offer-X")) {
                break;
            }
        }
        if (EnterInput.isOpen()) {
            EnterInput.initiate(amount);
        }
    }

    public static void offer(String item) {
        offer(1, item);
    }

    public static boolean addCoins(int amount) {
        InterfaceComponent component;
        if (EnterInput.isOpen()) {
            return EnterInput.initiate(amount);
        } else if ((component = OFFER_COINS_ADDRESS.resolve()) != null) {
            component.interact("Add from pouch");
        }
        return false;
    }

    public static boolean initiate(String... players) {
        if (isOpen()) {
            return false;
        }

        Player player = Players.getNearest(players);
        return player != null && player.interact("Trade with") && Time.sleepUntil(Trade::isOpen, 10000);
    }

    public static void transfer(String player, String... items) {
        if (containsAll(items)) {
            accept();
        } else if (isOpen()) {
            offer(28, items);
        } else {
            initiate(player);
        }
    }

    public static boolean isOpen() {
        return Interfaces.isOpen(INITIAL_TRADE_GROUP, CONFIRM_TRADE_GROUP);
    }

    public static boolean close() {
        InterfaceComponent component = Interfaces.getFirst(a -> TRADE_GROUPS.contains(a.getGroupIndex()) && a.containsAction("Close"));
        return component != null && component.interact("Close");
    }
}

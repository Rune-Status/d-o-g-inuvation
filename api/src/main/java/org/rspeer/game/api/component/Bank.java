package org.rspeer.game.api.component;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.api.ItemTables;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.position.Movement;
import org.rspeer.game.api.position.Positionable;
import org.rspeer.game.api.query.ItemQueryBuilder;
import org.rspeer.game.api.query.results.ItemQueryResults;
import org.rspeer.game.api.scene.Npcs;
import org.rspeer.game.api.scene.SceneObjects;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Bank {

    private static final List<String> SPECIAL_BANK_OBJECTS = Arrays.asList("Shantay chest");
    private static final List<String> BANK_OPTIONS = Arrays.asList("Bank", "Open", "Use");

    private static final int TABLE_KEY = ItemTables.BANK;
    private static final int BANK_INTERFACE = InterfaceComposite.BANK.getGroup();

    private static final int ITEM_CONTAINER_INDEX = 184;
    //184 = bank
    //14 = inv

    private static final InterfaceAddress ADDRESS = new InterfaceAddress(
            BANK_INTERFACE, ITEM_CONTAINER_INDEX
    );

    private static final InterfaceAddress CLOSE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(BANK_INTERFACE, a -> a.containsAction("Close") && a.isVisible())
    );

    private static final InterfaceAddress CARRIED_ITEMS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(BANK_INTERFACE, a -> a.containsAction("Deposit carried items"))
    );

    private static final InterfaceAddress WORN_ITEMS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(BANK_INTERFACE, a -> a.containsAction("Deposit worn items"))
    );

    private static final InterfaceAddress FAMILIAR_ITEMS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(BANK_INTERFACE, a -> a.containsAction("Deposit familiar items"))
    );

    private static final InterfaceAddress WITHDRAW_MODE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(BANK_INTERFACE, a -> a.containsAction(e -> e.startsWith("Switch to ")))
    );

    private static final InterfaceAddress PRESET_SETTINGS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(BANK_INTERFACE, a -> a.containsAction("Open Settings"), true)
    );

    private static final InterfaceAddress BACKPACK_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(BANK_INTERFACE, a -> a.containsParameter(ItemTables.BACKPACK))
    );

    private Bank() {
        throw new IllegalAccessError();
    }

    public static ItemQueryResults getItems(Predicate<Item> predicate) {
        return ItemTables.getItems(TABLE_KEY, ADDRESS, predicate.and(x -> x.getId() != 48447));
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
        return getFirst(predicate.and(x -> x.getStackSize() > 0)) != null;
    }

    public static boolean contains(int... ids) {
        return contains(Identifiable.predicate(ids));
    }

    public static boolean contains(String... names) {
        return contains(Identifiable.predicate(names));
    }

    public static boolean contains(Pattern... patterns) {
        return contains(Identifiable.predicate(patterns));
    }

    public static boolean containsAll(int... ids) {
        for (int id : ids) {
            if (!contains(id)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAll(String... names) {
        for (String name : names) {
            if (!contains(name)) {
                return false;
            }
        }
        return true;
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
        return Interfaces.isOpen(BANK_INTERFACE);
    }

    public static boolean open(boolean prioritizeNpc) {
        Interactable bank = SceneObjects.getNearest(e -> SPECIAL_BANK_OBJECTS.contains(e.getName()));
        if (bank == null) {
            Npc npc = Npcs.getNearest(a -> a.containsAction("Bank"));
            if (npc != null && prioritizeNpc) {
                bank = npc;
            } else {
                bank = SceneObjects.getNearest(a -> a.getName().startsWith("Bank"));
            }
        }
        if (bank == null) {
            return false;
        }
        if (((Positionable) bank).distance() > 10) {
            Movement.walkTo((Positionable) bank);
        } else {
            return bank.interact(BANK_OPTIONS::contains) && Time.sleepUntil(Bank::isOpen, 2000);
        }
        return false;
    }

    public static boolean open() {
        return open(true);
    }

    public static boolean close() {
        InterfaceComponent component = CLOSE_ADDRESS.resolve();
        return component != null && component.interact("Close");
    }

    public static boolean withdraw(Predicate<Item> predicate, int quantity) {
        Item item = getFirst(predicate.and(x -> x.getStackSize() > 0));
        if (item == null) {
            return false;
        }
        if (quantity == -1) {
            return item.interact("Withdraw-All");
        } else if (quantity == 1) {
            return item.interact("Withdraw-1");
        } else if (quantity == 10) {
            return item.interact("Withdraw-10");
        } else if (item.containsAction("Withdraw-" + quantity)) {
            return item.interact("Withdraw-" + quantity);
        } else if (EnterInput.isOpen()) {
            return EnterInput.initiate(quantity);
        } else {
            item.interact("Withdraw-X");
        }
        return false;
    }

    public static boolean withdraw(Predicate<Item> predicate) {
        return withdraw(predicate, 1);
    }

    public static boolean withdraw(String item, int quantity) {
        return withdraw(a -> a.getName().equals(item), quantity);
    }

    public static boolean withdraw(String... item) {
        return withdraw(Identifiable.predicate(item));
    }

    public static boolean withdraw(int... item) {
        return withdraw(Identifiable.predicate(item));
    }

    public static boolean withdraw(Pattern... item) {
        return withdraw(Identifiable.predicate(item));
    }

    public static boolean withdrawAll(Predicate<Item> predicate) {
        Item item = getFirst(predicate);
        return item != null && item.interact("Withdraw-All");
    }

    public static boolean withdrawAll(String... item) {
        return withdrawAll(Identifiable.predicate(item));
    }

    public static boolean withdrawAll(int... item) {
        return withdrawAll(Identifiable.predicate(item));
    }

    public static boolean withdrawAll(Pattern... item) {
        return withdrawAll(Identifiable.predicate(item));
    }

    public static boolean withdrawAllButOne(Predicate<Item> predicate) {
        Item item = getFirst(predicate.and(x -> x.getStackSize() > 0));
        return item != null && item.interact("Withdraw-All but one");
    }

    public static boolean withdrawAllButOne(String... item) {
        return withdrawAllButOne(Identifiable.predicate(item));
    }

    public static boolean withdrawAllButOne(int... item) {
        return withdrawAllButOne(Identifiable.predicate(item));
    }

    public static boolean withdrawAllButOne(Pattern... item) {
        return withdrawAllButOne(Identifiable.predicate(item));
    }

    public static boolean deposit(Predicate<Item> predicate, int quantity) {
        Item item = ItemTables.getFirst(ItemTables.BACKPACK, BACKPACK_ADDRESS, predicate);
        if (item == null) {
            return false;
        }

        if (quantity == -1) {
            return item.interact("Deposit-All");
        } else if (item.containsAction("Deposit-" + quantity)) {
            return item.interact("Deposit-" + quantity);
        } else if (quantity == 1) {
            return item.interact("Deposit-1");
        } else if (quantity == 5) {
            return item.interact("Deposit-5");
        } else if (quantity == 10) {
            return item.interact("Deposit-10");
        } else if (EnterInput.isOpen()) {
            return EnterInput.initiate(quantity);
        }

        item.interact("Deposit-X");
        return false;
    }

    public static boolean deposit(Predicate<Item> predicate) {
        return deposit(predicate, 1);
    }

    public static boolean deposit(String item, int quantity) {
        return deposit(a -> a.getName().equals(item), quantity);
    }

    public static boolean deposit(String... item) {
        return deposit(Identifiable.predicate(item));
    }

    public static boolean deposit(int... item) {
        return deposit(Identifiable.predicate(item));
    }

    public static boolean deposit(Pattern... item) {
        return deposit(Identifiable.predicate(item));
    }

    public static boolean depositAll(Predicate<Item> predicate) {
        ItemQueryResults results = ItemTables.getItems(ItemTables.BACKPACK, BACKPACK_ADDRESS, predicate);
        boolean success = true;
        for (Item item : results) { //TODO should this loop behaviour apply to withdrawAll too?
            //Actions are reverse-order so deposit-all will be last (if it is there)
            success &= item.interact(x -> x.contains("Deposit"));
        }
        return success;
    }

    public static boolean depositAll(String... item) {
        return depositAll(Identifiable.predicate(item));
    }

    public static boolean depositAll(int... item) {
        return depositAll(Identifiable.predicate(item));
    }

    public static boolean depositAll(Pattern... item) {
        return depositAll(Identifiable.predicate(item));
    }

    public static boolean depositInventory() {
        InterfaceComponent component = CARRIED_ITEMS_ADDRESS.resolve();
        return component != null && component.interact("Deposit carried items");
    }

    public static boolean depositEquipment() {
        InterfaceComponent component = WORN_ITEMS_ADDRESS.resolve();
        return component != null && component.interact("Deposit worn items");
    }

    public static boolean depositFamiliar() {
        InterfaceComponent component = FAMILIAR_ITEMS_ADDRESS.resolve();
        return component != null && component.interact("Deposit familiar items");
    }

    public static boolean loadPreset(int preset) {
        InterfaceComponent settings = PRESET_SETTINGS_ADDRESS.resolve();
        if (settings != null) {
            InterfaceComponent panel = Interfaces.getComponent(settings.getGroupIndex(), settings.getParentIndex());
            if (panel == null) {
                return false;
            }
            InterfaceComponent[] components = panel.getComponents(x -> x.containsAction("Load"));
            InterfaceComponent component;
            return components.length > 0 && (component = (components[preset - 1])) != null && component.interact("Load");
        }
        return false;
    }

    public static boolean setWithdrawMode(WithdrawMode mode) {
        if (mode == getWithdrawMode()) {
            return true;
        }
        InterfaceComponent component = Interfaces.resolve(WITHDRAW_MODE_ADDRESS);
        return component != null && component.interact(e -> e.startsWith("Switch to "));
    }

    public static WithdrawMode getWithdrawMode() {
        return WithdrawMode.values()[Varps.getValue(160)];
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> getItems().asList());
    }

    public static boolean isPlaceholdersEnabled() {
        return Varps.getBitValue(45190) != 0;
    }

    public enum WithdrawMode {
        ITEM, NOTE
    }
}

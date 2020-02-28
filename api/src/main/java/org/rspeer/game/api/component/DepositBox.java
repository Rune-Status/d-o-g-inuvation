package org.rspeer.game.api.component;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.api.commons.Predicates;
import org.rspeer.api.commons.Random;
import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.api.ItemTables;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.component.tab.Backpack;
import org.rspeer.game.api.input.Keyboard;
import org.rspeer.game.api.query.ItemQueryBuilder;
import org.rspeer.game.api.query.results.ItemQueryResults;
import org.rspeer.game.api.scene.SceneObjects;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public final class DepositBox {

    private static final int GROUP_INDEX = InterfaceComposite.DEPOSIT_BOX.getGroup();

    private static final InterfaceAddress ITEM_CONTAINER = new InterfaceAddress(GROUP_INDEX, 19);

    private static final InterfaceAddress CLOSE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Close"))
    );

    private static final InterfaceAddress WORN_ITEMS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Deposit Carried Items"))
    );

    private static final InterfaceAddress WORN_EQUIPMENT_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Deposit Worn Items"))
    );

    private static final InterfaceAddress FAMILIAR_ITEMS_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP_INDEX, a -> a.containsAction("Deposit Familiar's Items"))
    );

    private DepositBox() {
        throw new IllegalAccessError();
    }

    public static boolean isOpen() {
        return Interfaces.isOpen(GROUP_INDEX);
    }

    public static boolean open() {
        Interactable depositBox = SceneObjects.getNearest(a -> a.getName().equals("Deposit box") || a.getName().equals("Bank deposit box"));
        return depositBox != null && depositBox.interact("Deposit")
                && Time.sleepUntil(DepositBox::isOpen, 2000);
    }

    public static boolean close() {
        InterfaceComponent component = CLOSE_ADDRESS.resolve();
        return component != null && component.interact("Close");
    }

    public static boolean depositInventory() {
        InterfaceComponent component = WORN_ITEMS_ADDRESS.resolve();
        return component != null && component.interact("Deposit Carried Items");
    }

    public static boolean depositEquipment() {
        InterfaceComponent component = WORN_EQUIPMENT_ADDRESS.resolve();
        return component != null && component.interact("Deposit Worn Items");
    }

    public static boolean depositFamiliar() {
        InterfaceComponent component = FAMILIAR_ITEMS_ADDRESS.resolve();
        return component != null && component.interact("Deposit Familiar's Items");
    }

    private static ItemQueryResults getItems(Predicate<Item> predicate) {
        return ItemTables.getItems(ItemTables.BACKPACK, ITEM_CONTAINER, predicate);
    }

    private static Item getFirst(Predicate<Item> predicate) {
        return ItemTables.getFirst(ItemTables.BACKPACK, ITEM_CONTAINER, predicate);
    }

    public static boolean deposit(Predicate<Item> predicate, int amount) {
        Item item = getFirst(predicate);
        if (item == null) {
            return false;
        } else if (amount == -1) {
            return item.interact(a -> a.contains("Deposit-All"));
        }

        String action = "Deposit-" + String.valueOf(amount);
        if (item.containsAction(action)) {
            return item.interact(action);
        } else if (EnterInput.isOpen()) {
            if (EnterInput.getNumericEntry() == amount) {
                Keyboard.pressEventKey(KeyEvent.VK_ENTER);
                return Time.sleepUntil(EnterInput::isClosed, 2000);
            } else {
                Keyboard.sendText(String.valueOf(amount), true);
                return Time.sleepUntil(EnterInput::isClosed, 2000);
            }
        }
        if (item.interact(a -> a.contains("Deposit-X")) && Time.sleepUntil(EnterInput::isOpen, 2000)) {
            return false;
        }
        return false;
    }

    public static boolean deposit(String name, int amount) {
        return deposit(Identifiable.predicate(name), amount);
    }

    public static boolean deposit(int id, int amount) {
        return deposit(Identifiable.predicate(id), amount);
    }

    public static boolean depositAll(Predicate<Item> predicate) {
        ItemQueryResults items = getItems(predicate);
        if (items.size() == getItems(Predicates.always()).size()) {
            return depositInventory();
        }

        Set<Integer> visited = new HashSet<>();
        for (Item item : items) {
            if (!visited.contains(item.getId()) && item.getDefinition() != null) {
                if (!item.interact("Deposit-All")) {
                    return false;
                }
                visited.add(item.getId());
                Time.sleep(Random.mid(100, 200));
            }
        }
        return true;
    }

    public static boolean depositAll(String... names) {
        return depositAll(Identifiable.predicate(names));
    }

    public static boolean depositAll(int... ids) {
        return depositAll(Identifiable.predicate(ids));
    }

    public static boolean depositAllExcept(Predicate<Item> predicate) {
        return depositAll(predicate.negate());
    }

    public static boolean depositAllExcept(String... names) {
        return depositAllExcept(Identifiable.predicate(names));
    }

    public static boolean depositAllExcept(int... ids) {
        return depositAllExcept(Identifiable.predicate(ids));
    }

    public static boolean depositAll() {
        SceneObject depositBox = SceneObjects.getNearest(a -> a.containsAction("Deposit-All"));
        return depositBox != null && depositBox.interact("Deposit-All")
                && Time.sleepUntil(Backpack::isEmpty, 3000);
    }

    public static ItemQueryBuilder newQuery() {
        return new ItemQueryBuilder(() -> getItems(x -> true).asList());
    }
}

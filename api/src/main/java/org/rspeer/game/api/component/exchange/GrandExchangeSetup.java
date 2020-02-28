package org.rspeer.game.api.component.exchange;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.cache.ItemDefinition;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Definitions;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.component.EnterInput;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.component.Item;
import org.rspeer.game.api.component.tab.Backpack;
import org.rspeer.game.api.input.Keyboard;
import org.rspeer.game.providers.RSGrandExchangeOffer;

import java.util.function.Predicate;

public final class GrandExchangeSetup {

    private static final int ITEM_INDEX = 193;

    private static final int QUANTITY_TEXT_INDEX = 203;
    private static final int PRICE_TEXT_INDEX = 243;

    private static final int DECREASE_PRICE_INDEX = 252;
    private static final int INCREASE_PRICE_INDEX = 265;

    private static final int CONFIRM_INDEX = 302;

    private static final int SELECT_SCRIPT_ID = 11703;

    private GrandExchangeSetup() {
        throw new IllegalAccessError();
    }

    /**
     * @return The type of the current offer being set up.
     * If no offer is being set up, the default return is {@link RSGrandExchangeOffer.Type#EMPTY}
     */
    public static RSGrandExchangeOffer.Type getSetupType() {
        int type = Varps.getValue(139);
        switch (type) {
            case 0:
                return RSGrandExchangeOffer.Type.BUY;
            case 1:
                return RSGrandExchangeOffer.Type.SELL;
            default:
                return RSGrandExchangeOffer.Type.EMPTY;
        }
    }

    /**
     * @return The quantity of the current item in the setup screen
     */
    public static int getQuantity() {
        return getTextNumber(QUANTITY_TEXT_INDEX);
    }

    /**
     * @return The price of the current item in the setup screen
     */
    public static int getPricePerItem() {
        return getTextNumber(PRICE_TEXT_INDEX);
    }

    private static InterfaceComponent getComponent(int component) {
        return Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE.getGroup(), component);
    }

    private static int getTextNumber(int component) {
        InterfaceComponent quantity = getComponent(component);
        if (quantity == null) {
            return -1;
        }
        String text = quantity.getText()
                .replace(",", "")
                .replace("coins", "")
                .replace("coin", "")
                .replace("gp", "")
                .trim();
        return text.isEmpty() ? -1 : Integer.parseInt(text);
    }

    /**
     * @return The current item in the setup screen
     */
    public static Item getItem() {
        InterfaceComponent child = getComponent(ITEM_INDEX);
        return child != null && child.getItemId() != -1 && child.getItemId() != 6512
                ? new Item(-1, child.getItemId(), child.getItemStackSize(), child.toAddress())
                : null;
    }

    /**
     * @return {@code true} if the setup screen is open
     */
    public static boolean isOpen() {
        return getSetupType() != RSGrandExchangeOffer.Type.EMPTY;
    }

    /**
     * @param id The id of the desired item
     * @return Attempts to offer an item to place in the exchange
     * offer screen, returns {@code true} on successful interaction
     */
    public static boolean setItem(int id) {
        RSGrandExchangeOffer.Type type = getSetupType();
        if (type == RSGrandExchangeOffer.Type.BUY) {
            Game.fireScriptEvent(SELECT_SCRIPT_ID, id);
            return true;
        }
        InterfaceComponent geInventory = GrandExchange.getInventory();
        if (geInventory == null) {
            return false;
        }
        InterfaceComponent item = geInventory.getComponent(e -> e.getItemId() == id);
        return item != null && item.interact("Offer");
    }

    /**
     * Note: It is recommended to use {@link GrandExchangeSetup#setItem(int)} instead of this
     *
     * @param name The name of the desired item
     * @return Attempts to offer an item to place in the exchange
     * offer screen, returns {@code true} on successful interaction
     */
    public static boolean setItem(String name) {
        if (getSetupType() == RSGrandExchangeOffer.Type.BUY) {
            ItemDefinition definition = Definitions.getItem(name, e -> !e.isNoted());
            return definition != null && setItem(definition.getId());
        } else {
            Item item = Backpack.getFirst(name);
            return item != null && setItem(item.getId());
        }
    }

    /**
     * Note: It is recommended to use {@link GrandExchangeSetup#setItem(int)} instead of this
     *
     * @param name      The name of the desired item
     * @param predicate Due to name collisions, a predicate may be supplied to narrow down
     *                  the search for the item
     * @return Attempts to offer an item to place in the exchange
     * offer screen, returns {@code true} on successful interaction
     */
    public static boolean setItem(String name, Predicate<ItemDefinition> predicate) {
        ItemDefinition definition = Definitions.getItem(name,
                ((Predicate<ItemDefinition>) e -> !e.isNoted()).and(predicate));
        return definition != null && setItem(definition.getId());
    }

    /**
     * @param price The price of the item in the offer
     * @return Attempts to change the price of the current item in the offer,
     * returns {@code true} on successful interaction
     */
    public static boolean setPrice(int price) {
        InterfaceComponent setButton = getComponent(PRICE_TEXT_INDEX);
        if (setButton == null) {
            return false;
        }
        if (setButton.interact("Enter Number")
                && Time.sleepUntilForDuration(
                () -> Interfaces.isVisible(InterfaceComposite.GRAND_EXCHANGE.getGroup(),
                        PRICE_TEXT_INDEX - 2), 850, 2500)) {
            Keyboard.sendText(String.valueOf(price));
            Keyboard.pressEnter();
            return true;
        }
        return false;
    }

    /**
     * @param quantity The quantity of the item in the offer
     * @return Attempts to change the amount of the current item in the offer,
     * returns {@code true} on successful interaction
     */
    public static boolean setQuantity(int quantity) {
        InterfaceComponent setButton = getComponent(QUANTITY_TEXT_INDEX);
        if (setButton == null) {
            return false;
        }
        if (setButton.interact("Enter Number")
                && Time.sleepUntilForDuration(
                () -> Interfaces.isVisible(InterfaceComposite.GRAND_EXCHANGE.getGroup(),
                        QUANTITY_TEXT_INDEX - 2), 850, 2500)) {
            Keyboard.sendText(String.valueOf(quantity));
            Keyboard.pressEnter();
            return true;
        }
        return false;
    }

    /**
     * @param times The number of times to select the increase price button
     * @return Attempts to increase the price of the item by using the +5% button
     * returns {@code true} on successful interaction
     */
    public static boolean increasePrice(int times) {
        InterfaceComponent increaseButton = getComponent(INCREASE_PRICE_INDEX);
        if (increaseButton == null) {
            return false;
        }
        boolean result = true;
        for (int i = 0; i < times; i++) {
            result &= increaseButton.interact("Select");
        }
        return result;
    }

    /**
     * @param times The number of times to select the decrease price button
     * @return Attempts to decrease the price of the item by using the -5% button
     * returns {@code true} on successful interaction
     */
    public static boolean decreasePrice(int times) {
        InterfaceComponent decreaseButton = getComponent(DECREASE_PRICE_INDEX);
        if (decreaseButton == null) {
            return false;
        }
        boolean result = true;
        for (int i = 0; i < times; i++) {
            result &= decreaseButton.interact("Select");
        }
        return result;
    }

    /**
     * @return Selects the confirm button offer and returns {@code true} on successful interaction
     */
    public static boolean confirm() {
        InterfaceComponent confirm = Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE.getGroup(), CONFIRM_INDEX);
        return confirm != null && confirm.interact("Select");
    }
}
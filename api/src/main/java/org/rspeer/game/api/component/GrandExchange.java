package org.rspeer.game.api.component;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.cache.ItemDefinition;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Definitions;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.VarpComposite;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.component.tab.Backpack;
import org.rspeer.game.api.input.Keyboard;

import java.awt.event.KeyEvent;

/**
 * @deprecated pending rewrite
 */
public final class GrandExchange {

    private static int getStatus() {
        return Varps.getValue(VarpComposite.GRAND_EXCHANGE_STATE);
    }

    public static boolean isInBuyOffer() {
        return getStatus() == 0;
    }

    public static boolean isInSellOffer() {
        return getStatus() == 1;
    }

    public static boolean isInMainScreen() {
        return getStatus() == -1;
    }

    public static boolean openBuyOffer() {
        InterfaceComponent[] components = getBuySellComponents();
        if (components.length > 1) {
            components[0].interact("Select");
            return Time.sleepUntil(GrandExchange::isInBuyOffer, 1000);
        }
        return false;
    }

    public static boolean openSellOffer() {
        InterfaceComponent[] components = getBuySellComponents();
        if (components.length > 1) {
            components[1].interact("Select");
            return Time.sleepUntil(GrandExchange::isInSellOffer, 1000);
        }
        return false;
    }

    public static boolean collectToInventory() {
        InterfaceComponent comp = Interfaces.getComponent(651, 6);
        if (comp != null) {
            comp.interact("Select");
            return true;
        }
        return false;
    }

    public static boolean isUnlocked() {
        return Varps.getValue(101) == 2;
    }

    public static boolean isOpen() {
        return Interfaces.isVisible(105, 0);
    }

    private static int getQueuedId() {
        return Varps.getValue(VarpComposite.GRAND_EXCHANGE_OFFER_ITEM_ID);
    }

    private static int getPrice() {
        return Varps.getValue(VarpComposite.GRAND_EXCHANGE_OFFER_PRICE);
    }

    private static int getQuantity() {
        return Varps.getValue(VarpComposite.GRAND_EXCHANGE_OFFER_AMOUNT);
    }

    public static boolean sellItem(int id, int price, int quantity, int pMask) {
        ItemDefinition definition = Definitions.getItem(id);
        int invId = definition.isStackable() ? definition.getId() : definition.getNotedId();
        if (!isInSellOffer()) {
            openSellOffer();
            return false;
        }

        if (getQueuedId() != id) {
            queueItem(id, invId);
            return false;
        }

        if ((pMask & 0x1) == 1 && price == -1) {
            InterfaceComponent component = Interfaces.getFirst(105, e -> e.getText().endsWith(" gp") && e.getActions().length == 0);
            if (component != null) {
                int originalPrice = Integer.parseInt(component.getText().replace(" gp", ""));
                price = calcPrice(originalPrice, ((pMask & 0x2) == 2 ? 1.05D : 0.95D), pMask >> 2);
            }
        }

        if (getQuantity() != quantity && quantity != -1) {
            changeQuantity(quantity);
            return false;
        }

        if (getPrice() != price) {
            changePrice(price);
            return false;
        }

        return confirmOffer();
    }

    private static void queueItem(int id, int invId) {
        Item gayItem = Backpack.getFirst(invId);
        if (gayItem != null) {
            Item straightItem = new Item(gayItem.getIndex(), gayItem.getId(), gayItem.getStackSize(), new InterfaceAddress(107, 7));
            straightItem.interact("Offer");
            Time.sleepUntil(() -> getQueuedId() == id, 2000);
        }
    }

    private static int calcPrice(int price, double factor, int times) {
        if (times == 0) {
            return price;
        }
        return calcPrice((int) (price * factor), factor, times - 1);
    }

    public static boolean buyItem(int id, int price, int quantity) {
        if (!isInBuyOffer()) {
            openBuyOffer();
            return false;
        }

        if (getQueuedId() != id) {
            putBuyOffer(id);
            return false;
        }

        if (getQuantity() != quantity) {
            changeQuantity(quantity);
            return false;
        }

        if (getPrice() != price) {
            changePrice(price);
            return false;
        }

        return confirmOffer();
    }

    public static boolean confirmOffer() {
        InterfaceComponent component = Interfaces.getComponent(105, 300);
        if (component != null) {
            component.interact("Select");
            return Time.sleepUntil(GrandExchange::isInMainScreen, 2400);
        }
        return false;
    }

    public static boolean changeQuantity(int quantity) {
        InterfaceComponent component = Interfaces.getComponent(105, 203);
        if (component == null) {
            return false;
        }

        if (component.isVisible() && component.interact("Enter Number")) {
            Time.sleepUntil(component::isVisible, 1000);
            return false;
        }

        InterfaceComponent textComponent = Interfaces.getComponent(105, 201);
        if (textComponent == null) {
            return false;
        }

        if (textComponent.getText().equals("")) {
            Keyboard.sendText(String.valueOf(quantity));
            Time.sleepUntil(() -> textComponent.getText().equals(String.valueOf(quantity)), 2000);
            return false;
        }

        if (textComponent.getText().equals(String.valueOf(quantity))) {
            Keyboard.pressEnter();
            return Time.sleepUntil(() -> getQuantity() == quantity, 500);
        }

        int bound = textComponent.getText().length();
        for (int e = 0; e < bound; e++) {
            Keyboard.pressEventKey(KeyEvent.VK_BACK_SPACE);
        }
        Time.sleepUntil(() -> textComponent.getText().equals(""), 2000);
        return false;
    }

    public static boolean changePrice(int price) {
        InterfaceComponent component = Interfaces.getComponent(105, 243);
        if (component == null) {
            return false;
        }

        if (component.isVisible() && component.interact("Enter Number")) {
            Time.sleepUntil(component::isVisible, 1000);
            return false;
        }

        InterfaceComponent textComponent = Interfaces.getComponent(105, 241);
        if (textComponent == null) {
            return false;
        }

        if (textComponent.getText().equals("")) {
            Keyboard.sendText(String.valueOf(price));
            Time.sleepUntil(() -> textComponent.getText().equals(String.valueOf(price)), 2000);
            return false;
        }

        if (textComponent.getText().equals(String.valueOf(price))) {
            Keyboard.pressEnter();
            return Time.sleepUntil(() -> getPrice() == price, 500);
        }

        int bound = textComponent.getText().length();
        for (int e = 0; e < bound; e++) {
            Keyboard.pressEventKey(KeyEvent.VK_BACK_SPACE);
        }
        Time.sleepUntil(() -> textComponent.getText().equals(""), 2000);
        return false;
    }

    public static InterfaceComponent[] getBuySellComponents() {
        return Interfaces.getComponents(105, e -> e.isVisible() && e.containsAction("Select"));
    }

    private static final int OFFER_QUERY_SCRIPT_ID = 11703;

    public static boolean putBuyOffer(int val) {
        Game.fireScriptEvent(OFFER_QUERY_SCRIPT_ID, val);
        return Time.sleepUntil(() -> getQueuedId() == val, 1000);
    }
}

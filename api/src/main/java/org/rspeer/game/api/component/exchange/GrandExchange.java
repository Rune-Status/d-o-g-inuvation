package org.rspeer.game.api.component.exchange;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.query.GrandExchangeOfferQueryBuilder;
import org.rspeer.game.api.query.results.GrandExchangeOfferQueryResults;
import org.rspeer.game.api.scene.Npcs;
import org.rspeer.game.api.scene.SceneObjects;
import org.rspeer.game.providers.RSGrandExchangeOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class GrandExchange {

    private static final int INTERFACE_INDEX = InterfaceComposite.GRAND_EXCHANGE.getGroup();

    private static final int OFFER_SETUP_CONTAINER = 24;

    private static final int BACK_COMPONENT = 174;

    private static final int VIEW_VARP = 139; //0 = buy 1 = sell

    //138 = current offer index being viewed

    private GrandExchange() {
        throw new IllegalAccessError();
    }

    public static GrandExchangeOfferQueryResults getOffers(Predicate<? super RSGrandExchangeOffer> predicate) {
        List<RSGrandExchangeOffer> offers = new ArrayList<>();
        for (RSGrandExchangeOffer[] offer : Game.getClient().getGrandExchangeOffers()) {
            for (int i = 0; i < offer.length; i++) {
                RSGrandExchangeOffer off = offer[i];
                if (off != null && predicate.test(off)) {
                    off.setIndex(i);
                    offers.add(off);
                }
            }
        }
        return new GrandExchangeOfferQueryResults(offers);
    }

    public static GrandExchangeOfferQueryResults getOffers() {
        return getOffers(x -> true);
    }

    public static GrandExchangeOfferQueryBuilder newQuery() {
        return new GrandExchangeOfferQueryBuilder();
    }

    public static RSGrandExchangeOffer getFirst(Predicate<? super RSGrandExchangeOffer> predicate) {
        return getOffers(predicate).first();
    }

    public static GrandExchangeOfferQueryResults getOffers(RSGrandExchangeOffer.Type type) {
        return getOffers(x -> x.getType() == type);
    }

    public static RSGrandExchangeOffer getFirstActive() {
        return getFirst(x -> x.getType() != RSGrandExchangeOffer.Type.EMPTY);
    }

    public static RSGrandExchangeOffer getFirstEmpty() {
        return getFirst(x -> {
            if (x.getType() != RSGrandExchangeOffer.Type.EMPTY) {
                return false;
            }
            InterfaceComponent btn = Container.getBuyButton(x.getIndex());
            return btn != null && btn.isVisible();
        });
    }

    /**
     * @return {@code true} if the main grand exchange interface is open. It is
     * important to note that this does not return true for the grand
     * exchange history or sets interfaces
     */
    public static boolean isOpen() {
        return Interfaces.isVisible(INTERFACE_INDEX, 0);
    }

    /**
     * Creates an offer with the given type
     *
     * @param type The offer type
     * @return {@code true} if successfully created an offer
     */
    public static boolean createOffer(RSGrandExchangeOffer.Type type) {
        RSGrandExchangeOffer empty = getFirstEmpty();
        return empty != null && empty.create(type);
    }

    public static InterfaceComponent getInventory() {
        return Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE_INVENTORY.getGroup(), 0);
    }

    public static View getView() {
        InterfaceComponent component = Interfaces.getComponent(INTERFACE_INDEX, OFFER_SETUP_CONTAINER);
        if (component != null) {
            int type = Varps.getValue(VIEW_VARP);
            if (type == -1) {
                return View.OVERVIEW;
            }
            return type == 0 ? View.BUY_OFFER : View.SELL_OFFER;
        }
        return View.CLOSED;
    }

    private static int getOpenIndex() {
        int index = -1;
        GrandExchangeOfferQueryResults offers = getOffers(x -> true);
        for (int i = 0; i < offers.size(); i++) {
            RSGrandExchangeOffer offer = offers.get(i);
            if (offer.isEmpty()) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean open() {
        if (isOpen()) {
            return true;
        }
        Npc npc = Npcs.getNearest("Grand Exchange clerk");
        return npc != null && npc.interact("Exchange")
                && Time.sleepUntil(GrandExchange::isOpen, 2400);
    }

    public static boolean open(View view) {
        if (!isOpen()) {
            open();
            return false;
        }
        View current = getView();
        if (current == view) {
            return true;
        } else if (view == View.CLOSED) {
            //TODO close
        } else if (view == View.OVERVIEW) {
            InterfaceComponent back = Interfaces.getComponent(INTERFACE_INDEX, BACK_COMPONENT);
            return back != null && back.interact(x -> true);
        } else if (view == View.BUY_OFFER || view == View.SELL_OFFER) {
            if (current != View.OVERVIEW) {
                open(View.OVERVIEW);
                return false;
            }
            int open = getOpenIndex();
            if (open == -1) {
                return false; // no free slots
            }
            InterfaceComponent button = view == View.BUY_OFFER
                    ? Container.getBuyButton(open)
                    : Container.getSellButton(open);
            return button != null && button.interact(x -> true);
        }
        return false;
    }

    /**
     * @param toBank {@code true} to collect to the bank, {@code false} to collect to
     *               the inventory
     * @return Collects all items to the specified interface
     */
    public static boolean collectAll(boolean toBank) {
        InterfaceComponent collect = Interfaces.getComponent(InterfaceComposite.GRAND_EXCHANGE_COLLECT_ALL.getGroup(), toBank ? 14 : 6);
        return collect != null && collect.isVisible() && collect.interact("Select");
    }

    /**
     * @return Collects all items to the inventory
     */
    public static boolean collectAll() {
        return collectAll(false);
    }

    public enum View {
        OVERVIEW, BUY_OFFER, SELL_OFFER, CLOSED;
    }

    private static class Container {

        private static final int FIRST_BUY = 31;
        private static final int FIRST_SELL = 36;
        private static final int FIRST_VIEW = 24;

        private static final int SUB_VIEW_OFFER = 2;
        private static final int SUB_BUY_BUTTON = 3;
        private static final int SUB_SELL_BUTTON = 4;

        private static RSGrandExchangeOffer getOffer(int index) {
            try {
                return getOffers().get(index);
            } catch (Exception ignored) {
                return null;
            }
        }

        private static InterfaceComponent getBuyButton(int index) {
            return Interfaces.getComponent(INTERFACE_INDEX, FIRST_BUY + (index * 15));
        }

        private static InterfaceComponent getSellButton(int index) {
            return Interfaces.getComponent(INTERFACE_INDEX, FIRST_SELL + (index * 15));
        }

        private static InterfaceComponent getViewOfferButton(int index) {
            return Interfaces.getComponent(INTERFACE_INDEX, FIRST_VIEW + (index * 15));
        }
    }
}

package org.rspeer.game.providers;

import org.rspeer.api.commons.Random;
import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.cache.ItemDefinition;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Definitions;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;

public interface RSGrandExchangeOffer extends RSProvider {

    int getItemId();

    int getTransferredWealth();

    int getItemQuantity();

    int getItemPrice();

    int getTransferedQuantity();

    byte getState();

    int getIndex();

    @Deprecated
    void setIndex(int index);

    default ItemDefinition getItemDefinition() {
        return Definitions.getItem(getItemId());
    }

    default String getItemName() {
        ItemDefinition definition = getItemDefinition();
        return definition == null || definition.getName() == null ? "" : definition.getName();
    }

    default boolean isEmpty() {
        return getState() == 0;
    }

    default Type getType() {
        byte state = getState();
        if (state == 0) {
            return Type.EMPTY;
        } else if ((getState() & SELLING_MASK) == SELLING_MASK) {
            return Type.SELL;
        }
        return Type.BUY;
    }

    default boolean create(Type type) {
        Type current = getType();
        if (current != Type.EMPTY) {
            return false;
        }
        InterfaceComponent[] buysell = Interfaces.getComponents(105, e -> e.isVisible() && e.containsAction("Select"));
        return buysell.length > 0 && buysell[type.ordinal()].interact("Select");
    }

    int OFFER_COLLECT_CONTAINER = 23;

    int PROGRESS_MASK = 2;
    int FINISH_MASK = 4;
    int SELLING_MASK = 8;

    //TODO abort, view

    default boolean collect(CollectionAction action) {
        boolean success = true;
        for (InterfaceComponent cmp : Interfaces.getComponents(InterfaceComposite.GRAND_EXCHANGE.getGroup())) {
            if (cmp.getItemId() != -1 && cmp.containsAction(x -> x.contains("Collect"))) {
                success &= cmp.interact(cmp.containsAction(action.text)
                        ? x -> x.equals(action.text)
                        : x -> x.contains("Collect"));
                Time.sleep(Random.mid(60, 110));
            }
        }
        return success;
    }

    default Progress getProgress() {
        byte state = getState();
        if ((state & PROGRESS_MASK) == PROGRESS_MASK) {
            return Progress.IN_PROGRESS;
        } else if ((state & FINISH_MASK) == FINISH_MASK) {
            return Progress.FINISHED;
        }
        return Progress.EMPTY;
    }

    enum Type {
        BUY, SELL, EMPTY
    }

    enum Progress {
        EMPTY, IN_PROGRESS, FINISHED
    }

    enum CollectionAction {

        NOTE("Collect-notes"),
        ITEM("Collect-items");
        //BANK("Bank");

        private final String text;

        CollectionAction(String text) {
            this.text = text;
        }
    }
}
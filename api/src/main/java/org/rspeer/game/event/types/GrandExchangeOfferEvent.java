package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.event.listener.GrandExchangeOfferChangedListener;
import org.rspeer.game.event.listener.GrandExchangeOfferListener;
import org.rspeer.game.providers.RSGrandExchangeOffer;

public final class GrandExchangeOfferEvent extends Event<RSGrandExchangeOffer> {

    public GrandExchangeOfferEvent(RSGrandExchangeOffer source) {
        super(source, "Static");
    }

    public int getItemId() {
        return getSource().getItemId();
    }

    public int getTransferredWealth() {
        return getSource().getTransferredWealth();
    }

    public int getItemQuantity() {
        return getSource().getItemQuantity();
    }

    public int getItemPrice() {
        return getSource().getItemPrice();
    }

    public int getTransferedQuantity() {
        return getSource().getTransferedQuantity();
    }

    public byte getState() {
        return getSource().getState();
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof GrandExchangeOfferChangedListener) {
            ((GrandExchangeOfferChangedListener) listener).notify(this);
        }

        if (listener instanceof GrandExchangeOfferListener) {
            ((GrandExchangeOfferListener) listener).notify(this);
        }
    }
}

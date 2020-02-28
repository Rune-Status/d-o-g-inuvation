package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.GrandExchangeOfferEvent;

public interface GrandExchangeOfferListener extends EventListener {
    void notify(GrandExchangeOfferEvent e);
}
package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.GrandExchangeOfferEvent;

@Deprecated
public interface GrandExchangeOfferChangedListener extends EventListener {
    void notify(GrandExchangeOfferEvent e);
}
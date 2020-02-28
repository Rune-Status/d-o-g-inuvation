package org.rspeer.game.api.query.results;

import org.rspeer.game.providers.RSGrandExchangeOffer;

import java.util.Collection;
import java.util.Comparator;

public final class GrandExchangeOfferQueryResults extends QueryResults<RSGrandExchangeOffer, GrandExchangeOfferQueryResults> {

    public GrandExchangeOfferQueryResults(Collection<? extends RSGrandExchangeOffer> results) {
        super(results);
    }

    public GrandExchangeOfferQueryResults indexed() {
        return sort(Comparator.comparingInt(RSGrandExchangeOffer::getIndex));
    }
}

package org.rspeer.game.api.query;

import org.rspeer.game.api.commons.ArrayUtils;
import org.rspeer.game.api.component.exchange.GrandExchange;
import org.rspeer.game.api.query.results.GrandExchangeOfferQueryResults;
import org.rspeer.game.providers.RSGrandExchangeOffer;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class GrandExchangeOfferQueryBuilder extends QueryBuilder<RSGrandExchangeOffer, GrandExchangeOfferQueryBuilder, GrandExchangeOfferQueryResults> {

    private int[] ids = null;

    private RSGrandExchangeOffer.Type type = null;
    private RSGrandExchangeOffer.Progress progress = null;

    private String[] names;
    private String[] nameContains = null;

    private Integer price = null;

    @Override
    public Supplier<List<? extends RSGrandExchangeOffer>> getDefaultProvider() {
        return () -> GrandExchange.getOffers().asList();
    }

    @Override
    protected GrandExchangeOfferQueryResults createQueryResults(Collection<? extends RSGrandExchangeOffer> raw) {
        return new GrandExchangeOfferQueryResults(raw);
    }

    public GrandExchangeOfferQueryBuilder ids(int... ids) {
        this.ids = ids;
        return self();
    }

    public GrandExchangeOfferQueryBuilder type(RSGrandExchangeOffer.Type type) {
        this.type = type;
        return self();
    }

    public GrandExchangeOfferQueryBuilder progress(RSGrandExchangeOffer.Progress progress) {
        this.progress = progress;
        return self();
    }

    public GrandExchangeOfferQueryBuilder names(String... names) {
        this.names = names;
        return self();
    }

    public GrandExchangeOfferQueryBuilder nameContains(String... nameContains) {
        this.nameContains = nameContains;
        return self();
    }

    private GrandExchangeOfferQueryBuilder price(int price) {
        this.price = price;
        return self();
    }

    @Override
    public boolean test(RSGrandExchangeOffer offer) {
        if (ids != null && !ArrayUtils.contains(ids, offer.getItemId())) {
            return false;
        }

        if (type != null && offer.getType() != type) {
            return false;
        }

        if (progress != null && offer.getProgress() != progress) {
            return false;
        }

        if (names != null && !ArrayUtils.containsExactInsensitive(names, offer.getItemName())) {
            return false;
        }

        if (nameContains != null) {
            for (String name : nameContains) {
                if (offer.getItemName().toLowerCase().contains(name.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        if (price != null && offer.getItemPrice() != price) {
            return false;
        }

        return super.test(offer);
    }
}

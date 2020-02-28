package org.rspeer.game.api.query;

import org.rspeer.game.api.query.results.QueryResults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class QueryBuilder<K, QB extends QueryBuilder, QR extends QueryResults> implements Cloneable, Predicate<K> {

    private Supplier<List<? extends K>> provider;
    private Predicate<? super K> customFilter;

    public abstract Supplier<List<? extends K>> getDefaultProvider();

    public QR results() {
        List<? extends K> data = provider != null ? provider.get() : getDefaultProvider().get();
        List<K> filtered = new ArrayList<>();
        for (K elem : data) {
            if (test(elem)) {
                filtered.add(elem);
            }
        }
        return createQueryResults(filtered);
    }

    public final QB filter(Predicate<? super K> filter) {
        if (this.customFilter != null) {
            Predicate<? super K> old = this.customFilter;
            this.customFilter = (Predicate<K>) t -> old.test(t) && filter.test(t);
        } else {
            this.customFilter = filter;
        }
        return self();
    }

    public QB clone() {
        try {
            return (QB) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return self();
    }

    public final QB provider(Supplier<List<? extends K>> provider) {
        this.provider = provider;
        return self();
    }

    protected final QB self() {
        return (QB) this;
    }

    protected abstract QR createQueryResults(Collection<? extends K> raw);

    @Override
    public boolean test(K t) {
        return customFilter == null || customFilter.test(t);
    }
}

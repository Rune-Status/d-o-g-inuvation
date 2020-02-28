package org.rspeer.game.adapter;

import org.rspeer.game.providers.RSProvider;

import java.util.Objects;

/**
 * Abstract wrapper type
 *
 * @param <K> The type to wrap
 */
public abstract class Adapter<K extends RSProvider> {

    protected final K provider;

    protected Adapter(K provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    public K getProvider() {
        return provider;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RSProvider) {
            return o == provider;
        } else if (o instanceof Adapter) {
            return ((Adapter) o).provider == provider;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return provider.hashCode();
    }
}

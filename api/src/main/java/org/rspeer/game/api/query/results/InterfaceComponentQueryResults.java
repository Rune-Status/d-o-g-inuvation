package org.rspeer.game.api.query.results;

import org.rspeer.game.adapter.component.InterfaceComponent;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

public final class InterfaceComponentQueryResults extends QueryResults<InterfaceComponent, InterfaceComponentQueryResults> {

    public InterfaceComponentQueryResults(Collection<? extends InterfaceComponent> results) {
        super(results);
    }
}
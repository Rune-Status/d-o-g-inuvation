package org.rspeer.game.adapter.node;

import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.providers.RSNode;
import org.rspeer.game.providers.RSNodeDeque;

import java.util.Iterator;

public final class NodeDeque<K extends RSNode> extends Adapter<RSNodeDeque> implements Iterable<K> {

    private K root, current;

    public NodeDeque(RSNodeDeque provider) {
        super(provider);
        current = (K) provider.getHead();
        if (provider.getTail() != null) {
            root = (K) provider.getTail().getNext();
        }
    }

    public K getNext() {
        K last = current;
        if (last == root || last == null) {
            current = null;
            return null;
        }
        current = (K) last.getNext();
        return last;
    }

    @Override
    public Iterator<K> iterator() {
        return new NodeDequeIterator<>(provider);
    }
}

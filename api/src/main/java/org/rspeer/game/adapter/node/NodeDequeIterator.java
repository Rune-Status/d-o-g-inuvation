package org.rspeer.game.adapter.node;

import org.rspeer.game.providers.RSNode;
import org.rspeer.game.providers.RSNodeDeque;

import java.util.Iterator;

public final class NodeDequeIterator<K extends RSNode> implements Iterator<K> {

    private RSNode current;
    private RSNodeDeque deque;

    public NodeDequeIterator(RSNodeDeque deque) {
        this.deque = deque;
        RSNode tail = deque.getTail();
        if (tail != null) {
            current = tail.getNext();
        }
    }

    public K front() {
        reset();
        return next();
    }

    @Override
    public boolean hasNext() {
        return deque.getTail() != current;
    }

    @Override
    public K next() {
        RSNode linkable = current;
        if (deque.getTail() == linkable) {
            linkable = null;
            current = null;
        } else {
            current = linkable.getNext();
        }

        return (K) linkable;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void set(RSNodeDeque deque) {
        this.deque = deque;
        reset();
    }

    public void reset() {
        RSNode tail = deque.getTail();
        if (tail != null) {
            current = tail.getNext();
        }
    }
}
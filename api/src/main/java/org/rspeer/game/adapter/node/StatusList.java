package org.rspeer.game.adapter.node;

import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.providers.RSStatusList;
import org.rspeer.game.providers.RSStatusNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class StatusList<K extends RSStatusNode> extends Adapter<RSStatusList> {

    private final K head;
    private K current;

    public StatusList(RSStatusList provider) {
        super(provider);
        head = (K) provider.getHead();
        current = (K) provider.getCurrent();
    }

    public K getFirst() {
        RSStatusNode first = head.getNext();
        if (head == first) {
            current = null;
            return null;
        }
        current = (K) first.getNext();
        return (K) first;
    }

    public K getNext() {
        K current = this.current;
        if (head == current) {
            this.current = null;
            return null;
        }
        this.current = (K) current.getNext();
        return current;
    }

    public boolean isEmpty() {
        return head == head.getNext();
    }

    public K find(Predicate<K> predicate) {
        if (!isEmpty()) {
            K node = getFirst();
            while (node != null) {
                if (predicate.test(node)) {
                    return node;
                }
                node = getNext();
            }
        }
        return null;
    }

    public List<K> toList() {
        List<K> ts = new ArrayList<>();
        if (!isEmpty()) {
            K node = getFirst();
            while (node != null) {
                ts.add(node);
                node = getNext();
            }
        }
        return ts;
    }
}

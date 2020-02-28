package org.rspeer.api.collections;

import java.util.Iterator;

public class ArrayIterator<E> implements Iterator<E> {

    private E[] elements;

    private int lastIterated = 0;

    @SafeVarargs
    public ArrayIterator(E... elements) {
        this.elements = elements;
    }

    public int size() {
        return elements.length;
    }

    public E getAt(int index) {
        return elements[index];
    }

    @Override
    public boolean hasNext() {
        return lastIterated < elements.length;
    }

    @Override
    public E next() {
        return lastIterated < elements.length ? elements[lastIterated++] : null;
    }
}

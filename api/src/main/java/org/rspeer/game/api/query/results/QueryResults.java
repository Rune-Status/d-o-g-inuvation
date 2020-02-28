package org.rspeer.game.api.query.results;

import org.rspeer.api.commons.Random;

import java.util.*;
import java.util.function.Function;

public abstract class QueryResults<K, QR extends QueryResults> implements Collection<K> {

    protected final List<K> results;

    public QueryResults(Collection<? extends K> results) {
        if (results instanceof List) {
            this.results = (List<K>) results;
        } else {
            this.results = new ArrayList<>(results);
        }
    }

    public final QR sort(Comparator<? super K> comparator) {
        results.sort(comparator);
        return self();
    }

    protected final QR self() {
        return (QR) this;
    }

    public boolean retainAll(Collection<?> c) {
        return results.retainAll(c);
    }

    public List<K> asList() {
        return results;
    }

    public <E> QueryResults<E, QueryResults<E, ?>> map(Function<K, E> mapper) {
        List<E> mapped = new ArrayList<>();
        for (K elem : results) {
            mapped.add(mapper.apply(elem));
        }
        return new QueryResults<E, QueryResults<E, ?>>(mapped) {};
    }

    public K get(int index) {
        return results.get(index);
    }

    public K[] toArray(Object[] dest) {
        return (K[]) results.toArray(dest);
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int lastIndexOf(K o) {
        return results.lastIndexOf(o);
    }

    public boolean addAll(Collection<? extends K> c) {
        return results.addAll(c);
    }

    public final QR limit(int startIndex, int amount) {
        List<K> limit = new ArrayList<>(amount);

        for (int i = startIndex; i < size() && i - startIndex < amount; i++) {
            limit.add(get(i));
        }

        results.retainAll(limit);
        return self();
    }

    public final K first() {
        return size() == 0 ? null : get(0);
    }

    public void clear() {
        results.clear();
    }

    public int size() {
        return results.size();
    }

    public K[] toArray() {
        return (K[]) results.toArray();
    }

    public String toString() {
        return getClass().getSimpleName() + results;
    }

    public boolean removeAll(Collection<?> c) {
        return results.removeAll(c);
    }

    public boolean remove(Object o) {
        return results.remove(o);
    }

    public boolean add(K t) {
        return results.add(t);
    }

    public final QR reverse() {
        Collections.reverse(results);
        return self();
    }

    public final K last() {
        int index = size();
        return index != 0 ? get(index - 1) : null;
    }

    public int indexOf(K o) {
        return results.indexOf(o);
    }

    public final K random() {
        int index = size();
        return index != 0 ? get(Random.nextInt(index)) : null;
    }

    public boolean contains(Object o) {
        return results.contains(o);
    }

    public final QR shuffle() {
        Collections.shuffle(results);
        return self();
    }

    public boolean containsAll(Collection<?> c) {
        return results.containsAll(c);
    }

    public Iterator<K> iterator() {
        return results.iterator();
    }

    public final QR limit(int entries) {
        return limit(0, entries);
    }
}

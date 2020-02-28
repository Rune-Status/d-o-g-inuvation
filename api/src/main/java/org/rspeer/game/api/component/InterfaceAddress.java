package org.rspeer.game.api.component;

import org.rspeer.api.commons.ToBooleanFunction;
import org.rspeer.game.adapter.component.InterfaceComponent;

import java.util.function.*;

public final class InterfaceAddress {

    private static final InterfaceAddress BAD_ADDRESS = new InterfaceAddress(-1);

    private int root;
    private int component;
    private int subComponent;

    private SupplyOnce<InterfaceComponent> supplier;

    public InterfaceAddress(Supplier<InterfaceComponent> supplier) {
        this(-1, -1, -1);
        this.supplier = new SupplyOnce<>(supplier);
    }

    public InterfaceAddress(int root, int component, int subComponent) {
        this.root = root;
        this.component = component;
        this.subComponent = subComponent;
    }

    public InterfaceAddress(int root, int component) {
        this(root, component, -1);
    }

    public InterfaceAddress(int root) {
        this(root, -1);
    }

    private int map(Supplier<InterfaceComponent> supplier, ToIntFunction<InterfaceComponent> mapper) {
        InterfaceComponent comp = supplier.get();
        return comp == null ? -1 : mapper.applyAsInt(comp);
    }

    private void mapComponentInfo() {
        root = map(supplier, InterfaceComponent::getGroupIndex);
        component = map(supplier, x -> x.isGrandchild() ? x.getParentIndex() : x.getIndex());
        subComponent = map(supplier, InterfaceComponent::getComponentIndex);
    }

    public int getRoot() {
        if (supplier != null && !supplier.done()) {
            mapComponentInfo();
        }
        return root;
    }

    public int getComponent() {
        if (supplier != null && !supplier.done()) {
            mapComponentInfo();
        }
        return component;
    }

    public int getSubComponent() {
        if (supplier != null && !supplier.done()) {
            mapComponentInfo();
        }
        return subComponent;
    }

    public InterfaceAddress component(int component) {
        if (!isMapped()) {
            mapComponentInfo();
        }
        return new InterfaceAddress(root, component, -1);
    }

    public InterfaceAddress subComponent(int subComponent) {
        if (!isMapped()) {
            mapComponentInfo();
        }
        return new InterfaceAddress(root, component, subComponent);
    }

    public InterfaceAddress subComponent(int component, int subComponent) {
        if (!isMapped()) {
            mapComponentInfo();
        }
        return new InterfaceAddress(root, component, subComponent);
    }

    @Override
    public String toString() {
        return "InterfaceAddress[group=" + root + ",component=" + component + ",subcomponent=" + subComponent + "]";
    }

    public void ifPresent(Consumer<InterfaceComponent> action) {
        InterfaceComponent component = resolve();
        if (component != null && action != null) {
            action.accept(component);
        }
    }

    public InterfaceComponent resolve(Consumer<InterfaceComponent> action) {
        if (supplier != null && !supplier.done()) {
            mapComponentInfo();
        }

        InterfaceComponent component = Interfaces.resolve(this);
        if (component != null && action != null) {
            action.accept(component);
        }
        return component;
    }

    public <K> K map(Function<InterfaceComponent, K> mapper) {
        InterfaceComponent component = resolve();
        return component != null ? mapper.apply(component) : null;
    }

    public int mapToInt(ToIntFunction<InterfaceComponent> mapper) {
        InterfaceComponent component = resolve();
        return component != null ? mapper.applyAsInt(component) : -1;
    }

    public boolean mapToBoolean(ToBooleanFunction<InterfaceComponent> mapper) {
        InterfaceComponent component = resolve();
        return component != null && mapper.applyAsBoolean(component);
    }

    public InterfaceAddress filter(Predicate<InterfaceComponent> predicate) {
        InterfaceComponent component = resolve();
        return component != null && predicate.test(component) ? this : BAD_ADDRESS;
    }

    public InterfaceComponent resolve() {
        return resolve(null);
    }

    public boolean isMapped() {
        return supplier == null || supplier.done();
    }

    private class SupplyOnce<K> implements Supplier<K> {

        private final Supplier<K> base;
        private K cached;

        private SupplyOnce(Supplier<K> base) {
            this.base = base;
        }

        @Override
        public K get() {
            return cached != null ? cached : (cached = base.get());
        }

        public boolean done() {
            return cached != null;
        }
    }
}

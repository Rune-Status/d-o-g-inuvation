package org.rspeer.game.adapter.component;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.providers.RSInterface;
import org.rspeer.game.providers.RSInterfaceComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Interface extends Adapter<RSInterface> {

    private int index;

    public Interface(RSInterface provider) {
        super(provider);
    }

    public RSInterfaceComponent[] getRawComponents() {
        return Functions.mapOrDefault(() -> provider, RSInterface::getComponents, provider.getComponentsCopy());
    }

    public InterfaceComponent[] getComponents(Predicate<InterfaceComponent> predicate) {
        RSInterfaceComponent[] components = getRawComponents();
        List<InterfaceComponent> list = new ArrayList<>();
        if (components != null) {
            for (RSInterfaceComponent component : components) {
                if (component != null) {
                    InterfaceComponent adapter = component.getAdapter();
                    if (predicate.test(adapter)) {
                        list.add(adapter);
                    }
                }
            }
        }
        return list.toArray(new InterfaceComponent[0]);
    }

    public InterfaceComponent getComponent(Predicate<InterfaceComponent> predicate, boolean includeSubcomponents) {
        for (InterfaceComponent component : getComponents()) {
            if (predicate.test(component)) {
                return component;
            }

            if (includeSubcomponents) {
                InterfaceComponent sub = component.getComponent(predicate);
                if (sub != null) {
                    return sub;
                }
            }
        }
        return null;
    }

    public InterfaceComponent getComponent(Predicate<InterfaceComponent> predicate) {
        return getComponent(predicate, false);
    }

    public InterfaceComponent[] getComponents() {
        return getComponents(Predicates.always());
    }

    public InterfaceComponent getComponent(int index) {
        RSInterfaceComponent[] raw = getRawComponents();
        return raw != null && index >= 0 && index < raw.length && raw[index] != null
                ? raw[index].getAdapter() : null;
    }

    public boolean isClosed() {
        return provider.isClosed();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

package org.rspeer.game.api.component;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Predicates;
import org.rspeer.game.adapter.component.Interface;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Game;
import org.rspeer.game.providers.RSInterface;
import org.rspeer.game.providers.RSInterfaceNode;
import org.rspeer.game.providers.RSNodeTable;
import org.rspeer.game.api.query.InterfaceComponentQueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class Interfaces {

    private Interfaces() {
        throw new IllegalAccessError();
    }

    public static Interface[] getLoaded() {
        List<Interface> ifaces = new ArrayList<>();
        RSInterface[] raw = Game.getClient().getInterfaces();
        if (raw == null) {
            return new Interface[0];
        }

        int index = 0;
        for (RSInterface iface : raw) {
            if (iface != null) {
                Interface adapter = iface.getAdapter();
                adapter.setIndex(index);
                ifaces.add(adapter);
            }
            index++;
        }
        return ifaces.toArray(new Interface[0]);
    }

    public static List<RSInterfaceNode> getOpen() {
        List<RSInterfaceNode> open = new ArrayList<>();
        RSNodeTable<RSInterfaceNode> table = Game.getClient().getInterfaceNodes();
        if (table == null) return open;
        for (RSInterfaceNode node : table) {
            if (node.getState() != 0 && node.getState() != 3) {
                continue;
            }
            open.add(node);
        }
        return open;
    }

    public static boolean isOpen(int... indices) {
        for (RSInterfaceNode node : getOpen()) {
            for (int index : indices) {
                if (node.getUid() == index) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isOpen(int index) {
        for (RSInterfaceNode node : getOpen()) {
            if (node.getUid() == index) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVisible(int group, int component) {
        InterfaceComponent cmp = getComponent(group, component);
        return cmp != null && cmp.isVisible();
    }

    public static boolean isVisible(InterfaceAddress address) {
        return address.mapToBoolean(InterfaceComponent::isVisible);
    }

    public static Interface get(int index) {
        RSInterface[] raw = Game.getClient().getInterfaces();
        return raw != null && index >= 0 && index < raw.length && raw[index] != null
                ? raw[index].getAdapter() : null;
    }

    public static InterfaceComponent getComponent(int group, int component, int... subComponents) {
        Interface face = get(group);
        if (face == null) {
            return null;
        }
        InterfaceComponent comp = face.getComponent(component);
        for (int subComponent : subComponents) {
            if (comp == null) {
                break;
            }
            comp = comp.getComponent(subComponent);
        }
        return comp;
    }

    public static InterfaceComponent getFirst(Predicate<InterfaceComponent> predicate, boolean includeSubcomponents) {
        for (Interface iface : getLoaded()) {
            InterfaceComponent component = iface.getComponent(predicate);
            if (component != null) {
                return component;
            }

            if (includeSubcomponents) {
                for (InterfaceComponent layer : iface.getComponents()) {
                    component = layer.getComponent(predicate);
                    if (component != null) {
                        return component;
                    }
                }
            }
        }
        return null;
    }

    public static InterfaceComponent getFirst(Predicate<InterfaceComponent> predicate) {
        return getFirst(predicate, false);
    }

    public static InterfaceComponent getFirst(int group, Predicate<InterfaceComponent> predicate) {
        return getFirst(group, predicate, false);
    }

    public static InterfaceComponent getFirst(int group, Predicate<InterfaceComponent> predicate, boolean includeSubcomponents) {
        return Functions.mapOrNull(() -> get(group), iface -> iface.getComponent(predicate, includeSubcomponents));
    }

    public static InterfaceComponent[] getComponents(int group, Predicate<InterfaceComponent> predicate) {
        return Functions.mapOrDefault(() -> get(group), x -> x.getComponents(predicate), new InterfaceComponent[0]);
    }

    public static InterfaceComponent[] getComponents(int group) {
        return getComponents(group, Predicates.always());
    }

    public static InterfaceComponent[] getComponents(Predicate<InterfaceComponent> predicate) {
        List<InterfaceComponent> components = new ArrayList<>();
        for (Interface iface : getLoaded()) {
            Collections.addAll(components, iface.getComponents(predicate));
            for (InterfaceComponent comp : iface.getComponents()) {
                Collections.addAll(components, comp.getComponents(predicate));
            }
        }
        return components.toArray(new InterfaceComponent[0]);
    }

    public static InterfaceComponent resolve(InterfaceAddress address) {
        if (address == null || address.getComponent() == -1) {
            return null; //Root reference
        }

        if (address.getSubComponent() != -1) {
            return getComponent(address.getRoot(), address.getComponent(), address.getSubComponent());
        }

        return getComponent(address.getRoot(), address.getComponent());
    }

    public static InterfaceComponentQueryBuilder newQuery() {
        return new InterfaceComponentQueryBuilder();
    }
}

package org.rspeer.game.api.query;

import org.rspeer.game.adapter.component.Interface;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.commons.ArrayUtils;
import org.rspeer.game.api.query.results.InterfaceComponentQueryResults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class InterfaceComponentQueryBuilder extends QueryBuilder<InterfaceComponent, InterfaceComponentQueryBuilder, InterfaceComponentQueryResults> {

    private Predicate<String> text = null;
    private Predicate<String> action = null;
    private Predicate<String> name = null;

    private Boolean visible = null;

    private int[] materials = null;
    private int[] types = null;
    private int[] foregrounds = null;

    private boolean includeSubcomponents = false;

    @Override
    public Supplier<List<? extends InterfaceComponent>> getDefaultProvider() {
        return () -> {
            List<InterfaceComponent> components = new ArrayList<>();
            for (Interface group : Interfaces.getLoaded()) {
                for (InterfaceComponent component : group.getComponents()) {
                    components.add(component);
                    if (includeSubcomponents) {
                        Collections.addAll(components, component.getComponents());
                    }
                }
            }
            return components;
        };
    }

    @Override
    protected InterfaceComponentQueryResults createQueryResults(Collection<? extends InterfaceComponent> raw) {
        return new InterfaceComponentQueryResults(raw);
    }

    public InterfaceComponentQueryBuilder groups(int... groups) {
        return provider(() -> {
            List<InterfaceComponent> components = new ArrayList<>();
            for (int group : groups) {
                Interface face = Interfaces.get(group);
                if (face != null) {
                    for (InterfaceComponent component : face.getComponents()) {
                        components.add(component);
                        if (includeSubcomponents) {
                            Collections.addAll(components, component.getComponents());
                        }
                    }
                }
            }
            return components;
        });
    }

    public InterfaceComponentQueryBuilder components(int group, int... componentIndices) {
        includeSubcomponents = componentIndices.length > 1;
        return provider(() -> {
            List<InterfaceComponent> components = new ArrayList<>();
            for (int index : componentIndices) {
                InterfaceComponent component = Interfaces.getComponent(group, index);
                if (component != null) {
                    components.add(component);
                    if (includeSubcomponents) {
                        Collections.addAll(components, component.getComponents());
                    }
                }
            }
            return components;
        });
    }

    public InterfaceComponentQueryBuilder texts(Predicate<String> text) {
        this.text = text;
        return self();
    }

    public InterfaceComponentQueryBuilder names(Predicate<String> name) {
        this.name = name;
        return self();
    }

    public InterfaceComponentQueryBuilder actions(Predicate<String> action) {
        this.action = action;
        return self();
    }

    public InterfaceComponentQueryBuilder materials(int... materials) {
        this.materials = materials;
        return self();
    }

    public InterfaceComponentQueryBuilder types(int... types) {
        this.types = types;
        return self();
    }

    public InterfaceComponentQueryBuilder foregrounds(int... foregrounds) {
        this.foregrounds = foregrounds;
        return self();
    }

    public InterfaceComponentQueryBuilder includeSubcomponents() {
        includeSubcomponents = true;
        return self();
    }

    public InterfaceComponentQueryBuilder visible(boolean visible) {
        this.visible = visible;
        return self();
    }

    public InterfaceComponentQueryBuilder visible() {
        return visible(true);
    }

    @Override
    public boolean test(InterfaceComponent cmp) {
        if (materials != null && !ArrayUtils.contains(materials, cmp.getMaterialId())) {
            return false;
        }

        if (types != null && !ArrayUtils.contains(types, cmp.getType())) {
            return false;
        }

        if (visible != null && cmp.isVisible() != visible) {
            return false;
        }

        if (foregrounds != null && !ArrayUtils.contains(foregrounds, cmp.getForeground())) {
            return false;
        }

        if (text != null && !text.test(cmp.getText())) {
            return false;
        }

        if (name != null && !name.test(cmp.getName())) {
            return false;
        }

        if (action != null && !cmp.containsAction(action)) {
            return false;
        }

        return super.test(cmp);
    }
}

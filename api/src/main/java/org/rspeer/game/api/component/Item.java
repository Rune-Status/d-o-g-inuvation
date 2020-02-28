package org.rspeer.game.api.component;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.adapter.cache.ItemDefinition;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Definitions;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.action.tree.ComponentAction;

public final class Item implements Interactable, Identifiable {

    private final int index, id, stackSize;
    private final ItemDefinition definition;

    private final InterfaceAddress interfaceAddress;

    public Item(int index, int id, int stackSize, InterfaceAddress interfaceAddress) {
        this.index = index;
        this.id = id;
        this.stackSize = stackSize;
        this.interfaceAddress = interfaceAddress;

        definition = Definitions.getItem(id);
    }

    public Item(int stackSize, int index, int id) {
        this(index, id, stackSize, null);
    }

    public InterfaceComponent getComponent() {
        return interfaceAddress.subComponent(index).resolve();
    }

    public boolean isInteractable() {
        return interfaceAddress != null;
    }

    @Override
    public String getName() {
        return definition.getName();
    }

    @Override
    public String[] getActions() {
        return Functions.mapOrDefault(this::getComponent, InterfaceComponent::getActions, definition.getActions());
    }

    @Override
    public String[] getRawActions() {
        return Functions.mapOrDefault(this::getComponent, InterfaceComponent::getRawActions, definition.getActions());
    }

    @Override
    public boolean interact(String action) {
        return Functions.mapOrElse(this::getComponent, e -> e.interact(action));
    }

    @Override
    public boolean interact(int opcode) {
        return Functions.mapOrElse(this::getComponent, e -> e.interact(opcode));
    }

    @Override
    public ComponentAction actionOf(String action) {
        return Functions.mapOrNull(this::getComponent, e -> e.actionOf(action));
    }

    public int getIndex() {
        return index;
    }

    public int getId() {
        return id;
    }

    public int getStackSize() {
        return stackSize;
    }

    public ItemDefinition getDefinition() {
        return definition;
    }
}

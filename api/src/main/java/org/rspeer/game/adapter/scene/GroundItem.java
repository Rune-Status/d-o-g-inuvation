package org.rspeer.game.adapter.scene;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.adapter.cache.ItemDefinition;
import org.rspeer.game.api.Definitions;
import org.rspeer.game.api.action.ActionOpcodes;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.action.tree.Action;
import org.rspeer.game.api.action.tree.GroundItemAction;
import org.rspeer.game.api.position.*;
import org.rspeer.game.providers.RSGroundItem;

import java.time.Instant;

public final class GroundItem extends Adapter<RSGroundItem> implements Positionable, Identifiable, Interactable {

    private int x, y, floorLevel;

    private ItemDefinition definition;

    private Instant spawnTime;

    public GroundItem(RSGroundItem provider) {
        super(provider);
        spawnTime = Instant.now();
    }

    public int getQuantity() {
        return getProvider().getQuantity();
    }

    public int getId() {
        return getProvider().getId();
    }

    public String getName() {
        return getDefinition().getName();
    }

    public ItemDefinition getDefinition() {
        if (definition == null) {
            definition = Definitions.getItem(getId());
        }
        return definition;
    }

    public Instant getSpawnTime() {
        return spawnTime;
    }

    public void setPosition(int x, int y, int floorLevel) {
        this.x = x;
        this.y = y;
        this.floorLevel = floorLevel;
    }

    @Override
    public ScenePosition getScenePosition() {
        return getPosition().getScenePosition();
    }

    @Override
    public WorldPosition getPosition() {
        return Position.global(x, y, floorLevel);
    }

    @Override
    public FinePosition getAbsolutePosition() {
        return getPosition().getAbsolutePosition();
    }

    @Override
    public String[] getActions() {
        return Interactable.getFilteredActions(getRawActions());
    }

    @Override
    public String[] getRawActions() {
        return Functions.mapOrNull(this::getDefinition, ItemDefinition::getGroundActions);
    }

    @Override
    public boolean interact(int opcode) {
        ActionProcessor.submit(new GroundItemAction(opcode, getId(), getScenePosition().getX(), getScenePosition().getY()));
        return true;
    }

    @Override
    public Action actionOf(String action) {
        String[] actions = getRawActions();
        if (actions == null) {
            return null;
        }
        int index = Interactable.getActionIndex(actions, action);
        if (index < 0) {
            return null;
        }
        return new GroundItemAction(ActionOpcodes.OP_PICKABLE1 + index, getId(), getScenePosition().getX(), getScenePosition().getY());
    }

    @Override
    public boolean interact(String action) {
        Action resolved = actionOf(action);
        return resolved != null && interact(resolved.getOpcode());
    }
}

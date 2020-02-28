package org.rspeer.game.adapter.scene;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.action.ActionOpcodes;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.action.tree.Action;
import org.rspeer.game.api.action.tree.ObjectAction;
import org.rspeer.game.api.position.*;
import org.rspeer.game.providers.RSObjectDefinition;
import org.rspeer.game.providers.RSSceneEntity;
import org.rspeer.game.providers.RSSceneObject;
import org.rspeer.game.providers.RSVarpBit;

public final class SceneObject extends Adapter<RSSceneObject> implements Positionable, Interactable, Identifiable {

    private RSObjectDefinition definition;

    public SceneObject(RSSceneObject provider) {
        super(provider);
    }

    public int getId() {
        return provider.getId();
    }

    public int getType() {
        return provider.getType();
    }

    public int getOrientation() {
        return provider.getOrientation();
    }

    public long getUid() {
        int x = provider.getScenePosition().getX();
        int y = provider.getScenePosition().getY();
        if (provider instanceof RSSceneEntity) {
            x = ((RSSceneEntity) provider).getStartX();
            y = ((RSSceneEntity) provider).getStartY();
        }
        return Game.getClient().getObjectUid(provider, x, y);
    }

    public RSObjectDefinition getDefinition() {
        RSObjectDefinition raw = Game.getClient().getObjectDefinition(getId());
        if (raw != null) {
            RSObjectDefinition transformed = transform(raw);
            if (transformed != null) {
                definition = transformed;
            } else if (!raw.getName().equals("null")) {
                definition = raw;
            }
        }
        return definition;
    }

    public Area getArea() {
        if (provider instanceof RSSceneEntity) {
            RSSceneEntity e = (RSSceneEntity) provider;
            Position start = Position.regional(e.getStartX(), e.getStartY());
            Position end = Position.regional(e.getEndX(), e.getEndY());
            return Area.rectangular(start.getPosition(), end.getPosition(), e.getFloorLevel());
        }
        return Area.singular(getPosition());
    }

    private RSObjectDefinition transform(RSObjectDefinition raw) {
        int[] morphisms = raw.getTransformIds();
        if (morphisms == null) {
            return raw;
        }
        int index = -1;
        if (raw.getVarpBitIndex() != -1) {
            RSVarpBit bits = Varps.getBit(raw.getVarpBitIndex());
            if (bits != null) {
                index = Varps.getBitValue(raw.getVarpBitIndex());
            }
        } else if (raw.getVarpIndex() != -1) {
            index = Varps.getValue(raw.getVarpIndex());
        }
        if (index < 0 || index >= morphisms.length - 1) {
            int id = morphisms[morphisms.length - 1];
            return id != -1 ? Game.getClient().getObjectDefinition(id) : raw;
        } else if (morphisms[index] == -1) {
            return raw;
        }
        RSObjectDefinition def = Game.getClient().getObjectDefinition(morphisms[index]);
        return def != null ? def : raw;
    }

    public String getName() {
        if (getDefinition() == null || definition.getName() == null) {
            return "";
        }
        return definition.getName();
    }

    @Override
    public boolean interact(int opcode) {
        String name = getName();
        if (name != null) {
            int x = provider.getScenePosition().getX();
            int y = provider.getScenePosition().getY();
            if (provider instanceof RSSceneEntity) {
                x = ((RSSceneEntity) provider).getStartX();
                y = ((RSSceneEntity) provider).getStartY();
            }
            ActionProcessor.submit(new ObjectAction(opcode, getUid(), x, y));
        }
        return true;
    }

    @Override
    public ObjectAction actionOf(String action) {
        String[] actions = getRawActions();
        if (actions == null) {
            return null;
        }
        int index = Interactable.getActionIndex(actions, action);

        int x = provider.getScenePosition().getX();
        int y = provider.getScenePosition().getY();
        if (provider instanceof RSSceneEntity) {
            x = ((RSSceneEntity) provider).getStartX();
            y = ((RSSceneEntity) provider).getStartY();
        }
        if (index < 0) {
            if (action.equals(Game.getClient().getSelectedComponentAction())) {
                return new ObjectAction(ActionOpcodes.USE_ON_OBJ, getUid(), x, y);
            }
            return null;
        }

        int idx = ActionOpcodes.OP_OBJ1 + index;
        if (idx == 7) {
            idx = ActionOpcodes.OP_OBJ5;
        }
        return new ObjectAction(idx, getUid(), x, y);
    }

    @Override
    public boolean interact(String action) {
        Action resolved = actionOf(action);
        return resolved != null && interact(resolved.getOpcode());
    }


    @Override
    public String[] getActions() {
        return Interactable.getFilteredActions(getRawActions());
    }

    @Override
    public String[] getRawActions() {
        return Functions.mapOrNull(this::getDefinition, RSObjectDefinition::getActions);
    }

    public ScenePosition getScenePosition() {
        return provider.getScenePosition();
    }

    public WorldPosition getPosition() {
        return provider.getPosition();
    }

    @Override
    public FinePosition getAbsolutePosition() {
        return provider.getAbsolutePosition();
    }
}

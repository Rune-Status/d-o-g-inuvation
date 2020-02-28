package org.rspeer.game.adapter.scene;

import org.rspeer.api.commons.Functions;
import org.rspeer.game.adapter.cache.NpcDefinition;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.action.ActionOpcodes;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.action.tree.Action;
import org.rspeer.game.api.action.tree.NpcAction;
import org.rspeer.game.providers.RSNpc;
import org.rspeer.game.providers.RSNpcDefinition;

public final class Npc extends Mobile<RSNpc> implements Interactable {

    public Npc(RSNpc provider) {
        super(provider);
    }

    @Override
    public int getCombatLevel() {
        return Functions.mapOrM1(this::getDefinition, NpcDefinition::getCombatLevel);
    }

    public NpcDefinition getDefinition() {
        RSNpcDefinition def = provider.getDefinition();
        if (def != null) {
            RSNpcDefinition trans = def.transform();
            if (trans != null) {
                def = trans;
            }
        }
        return def != null ? def.getAdapter() : null;
    }

    @Override
    public int getId() {
        return Functions.mapOrM1(this::getDefinition, NpcDefinition::getId);
    }

    @Override
    public String getName() {
        return Functions.mapOrDefault(this::getDefinition, NpcDefinition::getName, "");
    }

    @Override
    public String[] getActions() {
        return Interactable.getFilteredActions(getRawActions());
    }

    @Override
    public String[] getRawActions() {
        return Functions.mapOrNull(this::getDefinition, NpcDefinition::getActions);
    }

    @Override
    public boolean interact(String action) {
        if (action.equals("Examine")) {
            return interact(ActionOpcodes.EXAMINE_NPC);
        }
        Action resolved = actionOf(action);
        return resolved != null && interact(resolved.getOpcode());
    }

    @Override
    public boolean interact(int opcode) {
        ActionProcessor.submit(new NpcAction(opcode, getIndex()));
        return true;
    }

    @Override
    public NpcAction actionOf(String action) {
        NpcDefinition definition = getDefinition();
        if (definition == null || getIndex() == -1) {
            return null;
        }
        int opcode = -1;
        int actionIndex = Interactable.getActionIndex(getRawActions(), action);
        if (actionIndex >= 0) {
            opcode = ActionOpcodes.OP_NPC1 + actionIndex;
        }
        if (action.equals("Examine")) {
            opcode = ActionOpcodes.EXAMINE_NPC;
        }
        if (action.equals(Game.getClient().getSelectedComponentAction())) {
            opcode = ActionOpcodes.USE_ON_NPC;
        }
        if (opcode == -1) {
            return null;
        }
        return new NpcAction(opcode, getIndex());
    }
}

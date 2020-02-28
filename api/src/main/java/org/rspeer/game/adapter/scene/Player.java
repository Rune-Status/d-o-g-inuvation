package org.rspeer.game.adapter.scene;

import org.rspeer.game.api.Game;
import org.rspeer.game.api.OverheadIcon;
import org.rspeer.game.api.action.ActionOpcodes;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.Interactable;
import org.rspeer.game.api.action.tree.Action;
import org.rspeer.game.api.action.tree.PlayerAction;
import org.rspeer.game.providers.RSPlayer;
import org.rspeer.game.providers.RSPlayerAppearance;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Player extends Mobile<RSPlayer> {

    public Player(RSPlayer provider) {
        super(provider);
    }

    @Override
    public int getCombatLevel() {
        return getP2pLevel();
    }

    public int getWildernessLevel() {
        return provider.getWildernessLevel();
    }

    public int getTotalLevel() {
        return provider.getTotalLevel();
    }

    public RSPlayerAppearance getAppearance() {
        return provider.getAppearance();
    }

    public byte getGender() {
        return provider.getGender();
    }

    @Override
    public int getId() {
        return provider.getIndex();
    }

    @Override
    public String getName() {
        String name = provider.getName();
        return name == null ? "" : name.replace('\u00A0', ' ');
    }

    public int getF2pLevel() {
        return provider.getF2pLevel();
    }

    public int getP2pLevel() {
        return provider.getP2pLevel();
    }

    @Override
    public String[] getActions() {
        return Interactable.getFilteredActions(getRawActions());
    }

    @Override
    public String[] getRawActions() {
        return Game.getClient().getPlayerActions();
    }

    @Override
    public boolean interact(int opcode) {
        ActionProcessor.submit(new PlayerAction(opcode, getIndex()));
        return true;
    }

    @Override
    public Action actionOf(String action) {
        String[] actions = getRawActions();
        if (action == null) {
            return null;
        }

        int index = Interactable.getActionIndex(actions, action);
        if (index < 0) {
            if (action.equals(Game.getClient().getSelectedComponentAction())) {
                return new PlayerAction(ActionOpcodes.USE_ON_PLAYER, getIndex());
            }
            return null;
        }
        return new PlayerAction(Game.getClient().getPlayerActionsIndexOffset()[index], getIndex());
    }

    public boolean isSkulled() {
        return isOverheadActive(x -> x.getType() == OverheadIcon.TYPE_SKULL);
    }

    public boolean isOverheadActive(Predicate<OverheadIcon> predicate) {
        for (OverheadIcon icon : getOverheadIcons()) {
            if (predicate.test(icon)) {
                return true;
            }
        }
        return false;
    }

    private List<OverheadIcon> getOverheadIcons() {
        List<OverheadIcon> icons = new ArrayList<>();
        int[] types = provider.getOverheadIcons();
        int[] flags = provider.getOverheadIconFlags();
        if (types == null || flags == null) {
            return icons;
        }

        for (int i = 0; i < types.length; i++) {
            icons.add(new OverheadIcon(i, types[i], flags[i]));
        }

        return icons;
    }

    @Override
    public boolean interact(String action) {
        Action resolved = actionOf(action);
        return resolved != null && interact(resolved.getOpcode());
    }
}

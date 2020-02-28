package org.rspeer.game.providers;

import org.rspeer.game.adapter.node.StatusList;
import org.rspeer.game.api.Game;

public interface RSCombatGauge extends RSStatusNode {

    RSStatusList getBarList();

    RSCombatGaugeDefinition getDefinition();

    RSMobile getOwner();

    default boolean isAdrenaline() {
        return getDefinition() != null && getDefinition().getField2() == 200;
    }

    default boolean isHealth() {
        return getDefinition() != null && getDefinition().getField2() == 250;
    }

    default boolean isProgressBar() {
        return getDefinition() != null && getDefinition().getField2() == 50;
    }

    default RSCombatBar getCombatBar() {
        StatusList list = new StatusList(getBarList());
        RSCombatBar first = (RSCombatBar) list.getFirst();
        if (first != null && first.getCycle() <= Game.getEngineCycle()) {
            RSCombatBar node = (RSCombatBar) list.getNext();
            while (node != null && node.getCycle() <= Game.getEngineCycle()) {
                first = node;
                node = (RSCombatBar) list.getNext();
            }
            return first;
        }
        return null;
    }
}
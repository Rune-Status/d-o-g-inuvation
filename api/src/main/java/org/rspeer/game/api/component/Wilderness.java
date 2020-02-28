package org.rspeer.game.api.component;

import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.scene.Players;

public final class Wilderness {

    private Wilderness() {
        throw new IllegalAccessError();
    }

    public static int getLevel() {
        return Players.getLocal().getWildernessLevel();
    }

    public static boolean isLocal() {
        InterfaceComponent iface = Interfaces.getComponent(381, 2);
        return iface != null && iface.getText().contains(": ");
    }
}

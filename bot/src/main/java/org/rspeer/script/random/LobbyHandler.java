package org.rspeer.script.random;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.script.Script;

public class LobbyHandler extends ScriptDaemon {

    private InterfaceComponent playNowButton;

    public LobbyHandler(Script script) {
        super(script);
    }

    @Override
    public String name() {
        return "Lobby";
    }

    @Override
    public int execute() {
        playNowButton.interact("Play");
        Time.sleepUntil(Game::isLoggedIn, 2000);
        return 1000;
    }

    @Override
    public boolean validate() {
        playNowButton = Interfaces.getFirst(906, a -> a.containsAction("Play"));
        return playNowButton != null;
    }
}

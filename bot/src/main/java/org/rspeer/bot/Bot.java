package org.rspeer.bot;

import org.rspeer.arguments.Arguments;
import org.rspeer.event.impl.EventDispatcher;
import org.rspeer.game.event.callback.ClientCallbackHandler;
import org.rspeer.io.LogFileBuffer;
import org.rspeer.loader.GameConfiguration;
import org.rspeer.script.ScriptController;
import org.rspeer.ui.BotView;

public interface Bot {

    GameConfiguration getGameConfiguration();

    BotView getView();

    void initializeView();

    void start();

    ScriptController getScriptController();

    EventDispatcher getEventDispatcher();

    ClientCallbackHandler getCallbackHandler();

    Arguments getArguments();

    void toggleFileLogging(boolean enabled);

    LogFileBuffer getLogFileBuffer();
}

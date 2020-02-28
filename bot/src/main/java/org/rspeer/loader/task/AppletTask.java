package org.rspeer.loader.task;

import org.rspeer.api.concurrent.Tasks;
import org.rspeer.bot.Bot;
import org.rspeer.bot.BotTask;
import org.rspeer.game.api.Definitions;
import org.rspeer.game.api.Game;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.io.CachedClassLoader;
import org.rspeer.loader.ClientApplet;
import org.rspeer.loader.GameStub;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class AppletTask extends BotTask {

    private final Map<String, byte[]> injected;
    private final List<EventListener> listeners;

    public AppletTask(Bot bot, Map<String, byte[]> injected, List<EventListener> listeners) {
        super(bot);
        this.injected = injected;
        this.listeners = listeners;
    }

    @Override
    public void run() {
        ClientApplet applet = new ClientApplet(new CachedClassLoader(injected));
        applet.setStub(new GameStub(bot.getGameConfiguration()));

        for (EventListener listener : listeners) {
            bot.getEventDispatcher().register(listener);
        }

        Game.getClient().setEventDispatcher(bot.getEventDispatcher());
        Game.getClient().setCallbackHandler(bot.getCallbackHandler());
        bot.getView().notifySupplied(applet);

        Tasks.schedule(new GameLoadedTask(bot), 3, TimeUnit.SECONDS);
        Tasks.schedule(this::setExceptionHandler, 3, TimeUnit.SECONDS);
    }

    private void setExceptionHandler() {
        Thread.UncaughtExceptionHandler original = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            if (original != null) {
                original.uncaughtException(t, e);
            }

            if (!(e instanceof ThreadDeath)) {
                Writer exception = new StringWriter();
                PrintWriter printWriter = new PrintWriter(exception);
                e.printStackTrace(printWriter);
                e.printStackTrace(System.out);

                if (Definitions.isLoaded()) {
                    Game.getClient().printToConsole(exception.toString());
                }
            }
        });
    }

    @Override
    public String verbose() {
        return "Initializing applet...";
    }
}

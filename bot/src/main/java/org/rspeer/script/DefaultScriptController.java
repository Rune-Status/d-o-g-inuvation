package org.rspeer.script;

import org.rspeer.api.concurrent.Task;
import org.rspeer.api.concurrent.Tasks;
import org.rspeer.bot.Bot;
import org.rspeer.game.api.Game;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.script.random.LobbyHandler;
import org.rspeer.script.random.LoginHandler;
import org.rspeer.script.random.ScriptDaemon;
import org.rspeer.script.random.Restart23hHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public final class DefaultScriptController extends ScriptController {

    private ScriptThread scriptThread;

    public DefaultScriptController(Bot bot) {
        super(bot);
    }

    @Override
    public Script getActiveScript() {
        return active;
    }

    @Override
    public boolean setActiveScript(Script active) {
        if (this.active == null || this.active.getState() == Script.State.STOPPED) {
            listeners.forEach(x -> x.notify(this.active, active));
            this.active = active;
            this.active.getController().updateStartTime();

            scriptThread = new ScriptThread(active,
                    new Restart23hHandler(active),
                    new LoginHandler(active),
                    new LobbyHandler(active)
            );

            scriptThread.start();
            if (active instanceof EventListener) {
                Game.getClient().getEventDispatcher().register((EventListener) active);
            }

            try {
                Class<? extends Script> clazz = active.getPassiveScript();
                if (clazz != null) {
                    Script passive;
                    if (clazz.getEnclosingClass() == null || Modifier.isStatic(clazz.getModifiers())) {
                        Constructor<? extends Script> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        passive = constructor.newInstance();
                    } else if (clazz.getEnclosingClass() == active.getClass()) {
                        Constructor<? extends Script> constructor = clazz.getDeclaredConstructor(active.getClass());
                        constructor.setAccessible(true);
                        passive = constructor.newInstance(active);
                    } else {
                        throw new UnsupportedOperationException("Unsupported passive script constructor: "
                                + Arrays.toString(clazz.getDeclaredConstructors())
                        );
                    }

                    ScriptThread background = new ScriptThread(passive);
                    background.start();
                    if (passive instanceof EventListener) {
                        Game.getClient().getEventDispatcher().register((EventListener) passive);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    public void notifyState(Script.State state) {
        switch (state) {
            case STOPPED:
            case PAUSED: {
                bot.getView().getMenuBar().scriptStopped();
                bot.getView().getMenuBar().scriptStopped();
                break;
            }
        }
    }

    @Override
    public void stopActiveScript() {
        if (active != null) {
            active.setState(Script.State.STOPPED);
            if (active instanceof EventListener) {
                Game.getClient().getEventDispatcher().deregister((EventListener) active);
            }
            active = null;
        }
    }

    @Override
    public void schedule(Script script, long after, TimeUnit unit) {
        Tasks.schedule(new Task() {
            @Override
            public void run() {
                stopActiveScript();
                setActiveScript(script);
            }

            @Override
            public String verbose() {
                ScriptMeta meta = script.getMeta();
                return "Script - " + (meta != null ? meta.name() : script.getClass().getSimpleName());
            }
        }, after, unit);
    }

    @Override
    public void removeDaemon(Class<? extends ScriptDaemon> clazz) {
        for (ScriptDaemon daemon : scriptThread.daemons) {
            if (daemon.getClass().equals(clazz)) {
                scriptThread.daemons.remove(daemon);
                break;
            }
        }
    }

    @Override
    public void addDaemon(ScriptDaemon event) {
        removeDaemon(event.getClass());
        scriptThread.daemons.add(event);
    }
}

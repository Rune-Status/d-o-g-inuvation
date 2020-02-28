package org.rspeer.script;

import org.rspeer.api.commons.Time;
import org.rspeer.bot.Bot;
import org.rspeer.game.api.scene.Projection;
import org.rspeer.script.provider.ScriptSource;
import org.rspeer.script.random.ScriptDaemon;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ScriptController {

    private static ScriptController instance;

    protected final Bot bot;
    protected final List<ScriptChangeListener> listeners;

    protected Script active;

    private ScriptSource source;
    private String[] args;
    private Instant scriptStartTime = Instant.now();

    public ScriptController(Bot bot) {
        this.bot = bot;
        listeners = new CopyOnWriteArrayList<>();
    }

    //TODO absolutely disgusting
    public static ScriptController getInstance(Bot bot) {
        if (instance == null) {
            instance = new DefaultScriptController(bot);
        }
        return instance;
    }

    public static ScriptController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ScriptController initialization error");
        }
        return instance;
    }

    public ScriptSource getSource() {
        return source;
    }

    public void setSource(ScriptSource source) {
        this.source = source;
    }

    public abstract Script getActiveScript();

    public abstract boolean setActiveScript(Script script);

    public abstract void stopActiveScript();

    public abstract void notifyState(Script.State state);

    public abstract void schedule(Script script, long after, TimeUnit unit);

    public void addListener(ScriptChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ScriptChangeListener listener) {
        listeners.remove(listener);
    }

    public abstract void removeDaemon(Class<? extends ScriptDaemon> clazz);

    public abstract void addDaemon(ScriptDaemon event);

    public final String[] getArgs() {
        return args;
    }

    public final void setArgs(String[] args) {
        this.args = args;
        if (args != null && active != null) {
            active.processArgs(args);
        }
    }

    public final void updateStartTime() {
        this.scriptStartTime = Instant.now();
    }

    public final Instant getScriptStartTime() {
        return scriptStartTime;
    }

    static class ScriptThread extends Thread {

        final Script instance;
        final List<ScriptDaemon> daemons;

        ScriptThread(Script instance, ScriptDaemon... daemons) {
            this.instance = instance;
            this.daemons = new LinkedList<>();

            Collections.addAll(this.daemons, daemons);
        }

        @Override
        public void run() {
            instance.setState(Script.State.STARTING);

            try {
                //TODO change this, it currently stops active script
                //if a passive script stops


                AtomicBoolean started = new AtomicBoolean(false);
                EventQueue.invokeLater(() -> {
                    if (!instance.start()) {
                        instance.getController().stopActiveScript();
                    } else {
                        started.set(true);
                    }
                });

                while (instance.getState() != Script.State.STOPPED) {
                    if (!started.get()) {
                        continue;
                    }
                    int delay = 1000;
                    if (instance.getState() != Script.State.PAUSED) {
                        ScriptDaemon currentDaemon = null;
                        for (ScriptDaemon daemon : daemons) {
                            if (daemon.validate()) {
                                currentDaemon = daemon;
                                break;
                            } else {
                                daemon.reset();
                            }
                        }
                        if (currentDaemon != null) {
                            delay = currentDaemon.execute();
                        } else {
                            delay = instance.loop();
                        }
                        if (delay == -1) {
                            break;
                        }
                    }
                    Time.sleep(delay);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Time.sleep(100);
            }

            Projection.setEngineTickDelay(Projection.DEFAULT_ENGINE_TICK_DELAY);
            instance.getController().stopActiveScript();
        }
    }
}

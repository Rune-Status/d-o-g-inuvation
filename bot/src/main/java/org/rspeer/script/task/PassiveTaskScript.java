package org.rspeer.script.task;

import org.rspeer.game.api.Game;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptController;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class PassiveTaskScript extends Script {

    private static final int DEFAULT_LOOP_DELAY = 300;
    private final List<Task> tasks;
    private int loopDelay = DEFAULT_LOOP_DELAY;

    public PassiveTaskScript() {
        tasks = new CopyOnWriteArrayList<>();
        setAntiIdleActive(false);
    }

    public abstract boolean onStart();

    @Override
    public int loop() {
        return DEFAULT_LOOP_DELAY;
    }

    public final void submit(Task... tasks) {
        for (Task task : tasks) {
            submit(task);
        }
    }

    private void submit(Task task) {
        tasks.add(task);
        tasks.sort(Comparator.comparingInt(Task::getPriority));
        if (task instanceof EventListener) {
            Game.getEventDispatcher().register((EventListener) task);
        }

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
                    if (ScriptController.getInstance().getActiveScript() == null) {
                        service.shutdown();
                        return;
                    }

                    try {
                        if (task.validate()) {
                            //Time.sleep(task.execute());
                            task.execute(); //already delays for loopDelay? dont need sleep
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 150, loopDelay, TimeUnit.MILLISECONDS
        );
    }

    public void setLoopDelay(int loopDelay) {
        this.loopDelay = loopDelay;
    }
}

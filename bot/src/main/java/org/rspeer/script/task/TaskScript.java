package org.rspeer.script.task;

import org.rspeer.game.api.Game;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptChangeListener;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class TaskScript extends Script {

    private static final int DEFAULT_LOOP_DELAY = 300;

    private final List<Task> tasks;

    private Task previous;
    private Task current;

    public TaskScript() {
        tasks = new CopyOnWriteArrayList<>();
        getController().addListener(new ScriptChangeListener() {
            @Override
            public void notify(Script previous, Script current) {
                for (Task task : tasks) {
                    if (task instanceof EventListener) {
                        Game.getEventDispatcher().deregister((EventListener) task);
                    }
                }
                getController().removeListener(this);
            }
        });
    }

    public abstract boolean onStart();

    @Override
    public int loop() {
        Task current = null;
        for (Task task : tasks) {
            if (task.validate()) {
                if (task != previous) {
                    if (this instanceof TaskChangeListener) {
                        ((TaskChangeListener) this).notify(previous, task);
                    }

                    if (task instanceof TaskChangeListener) {
                        ((TaskChangeListener) task).notify(previous, task);
                    }

                    if (previous instanceof TaskChangeListener) {
                        ((TaskChangeListener) previous).notify(previous, task);
                    }
                }
                current = task;
                previous = task;
                break;
            }
        }
        return (this.current = current) != null ? current.execute() : DEFAULT_LOOP_DELAY;
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
    }

    public final void remove(Task... tasks) {
        for (Task task : tasks) {
            this.tasks.remove(task);
            if (task instanceof EventListener) {
                Game.getEventDispatcher().deregister((EventListener) task);
            }
        }
    }

    public Task getCurrent() {
        return current;
    }
}

package org.rspeer.api.concurrent;

import org.rspeer.api.commons.ExecutionService;
import net.jodah.failsafe.function.CheckedRunnable;

import java.util.concurrent.TimeUnit;

public final class Tasks {

    private Tasks() {
        throw new IllegalAccessError();
    }

    public static void execute(CheckedRunnable task) {
        ExecutionService.execute(task);
    }

    public static void execute(Task task) {
        String msg = task.verbose();
        if (!msg.isEmpty()) {
            System.out.println(msg);
        }
        ExecutionService.execute(task);
    }

    public static void schedule(CheckedRunnable task, long delay, TimeUnit unit) {
        ExecutionService.schedule(task, delay, unit);
    }

    public static void schedule(Task task, long delay, TimeUnit unit) {
        String msg = task.verbose();
        if (!msg.isEmpty()) {
            System.out.println("Scheduled after " + delay + " " + unit.name().toLowerCase() + ": " + msg);
        }
        ExecutionService.schedule(() -> {
            if (!msg.isEmpty()) {
                System.out.println(msg);
            }

            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay, unit);
    }
}

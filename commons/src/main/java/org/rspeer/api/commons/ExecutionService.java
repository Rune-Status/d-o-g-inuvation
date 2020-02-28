package org.rspeer.api.commons;

import net.jodah.failsafe.function.CheckedRunnable;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class ExecutionService {

    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors() + 1);

    public static void execute(CheckedRunnable runnable, Consumer<Throwable> error) {
        executor.execute(tryExecute(runnable, error));
    }

    public static void execute(CheckedRunnable runnable) {
        executor.execute(tryExecute(runnable, DEFAULT_THROWABLE_CONSUMER));
    }

    public static void executeWithRetry(CheckedRunnable runnable) {
        executor.execute(executeWithRetry(runnable, DEFAULT_THROWABLE_CONSUMER));
    }

    public static void executeWithRetry(CheckedRunnable runnable, int tries) {
        executor.execute(executeWithRetry(runnable, tries, DEFAULT_THROWABLE_CONSUMER));
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(CheckedRunnable runnable, long delay, long interval, TimeUnit unit) {
        return executor.scheduleAtFixedRate(tryExecute(runnable), delay, interval, unit);
    }

    public static<V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        return executor.schedule(tryExecuteCallable(callable), delay, unit);
    }

    public static ScheduledFuture<?> schedule(CheckedRunnable runnable, long delay, TimeUnit unit) {
        return executor.schedule(tryExecute(runnable), delay, unit);
    }

    public static void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(10, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate.");
            }
        } catch (InterruptedException ignored) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static Runnable tryExecute(CheckedRunnable runnable, Consumer<Throwable> error) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                error.accept(e);
            }
        };
    }

    private static Runnable executeWithRetry(CheckedRunnable runnable, int tries, Consumer<Throwable> error) {
        return () -> {
            try {
                Failsafe.execute(runnable, tries);
            } catch (Throwable e) {
                error.accept(e);
            }
        };
    }

    private static Runnable executeWithRetry(CheckedRunnable runnable, Consumer<Throwable> error) {
        return executeWithRetry(runnable, Failsafe.DEFAULT_RETRIES, error);
    }

    private static<V> Callable<V> tryExecuteCallable(Callable<V> callable, Consumer<Throwable> error) {
        return () -> {
            try {
                return callable.call();
            } catch (Throwable e) {
                error.accept(e);
                return null;
            }
        };
    }

    private static<V> Callable<V> tryExecuteCallable(Callable<V> callable) {
        return tryExecuteCallable(callable, DEFAULT_THROWABLE_CONSUMER);
    }

    private static Runnable tryExecute(CheckedRunnable runnable) {
        return tryExecute(runnable, DEFAULT_THROWABLE_CONSUMER);
    }

    private static Consumer<Throwable> DEFAULT_THROWABLE_CONSUMER = Throwable::printStackTrace;

}

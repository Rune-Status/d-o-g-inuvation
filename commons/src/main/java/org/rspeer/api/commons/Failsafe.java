package org.rspeer.api.commons;

import net.jodah.failsafe.FailsafeExecutor;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.event.ExecutionAttemptedEvent;
import net.jodah.failsafe.function.CheckedConsumer;
import net.jodah.failsafe.function.CheckedRunnable;
import net.jodah.failsafe.function.ContextualSupplier;

import java.time.temporal.ChronoUnit;

public class Failsafe {

    public static final int DEFAULT_RETRY_RATE = 1;
    public static final int MAX_BACK_OFF = 10;
    public static final int DEFAULT_RETRIES = 10000;
    public static final Class<? extends Throwable> DEFAULT_EXCEPTION = Throwable.class;
    public static final CheckedConsumer<? extends ExecutionAttemptedEvent<Object>> DEFAULT_ON_RETRY = s -> {
        if (s.getLastFailure() != null) {
            System.out.println(s.getLastFailure().getMessage());
        }
    };
    private static boolean isShuttingDown;

    private static FailsafeExecutor<Object> getExecutor(int tries, int delay, Class<? extends Throwable> exception,
            CheckedConsumer<? extends ExecutionAttemptedEvent<Object>> onRetry) {
        RetryPolicy<Object> retryPolicy = new RetryPolicy<>()
                .handle(exception)
                .withBackoff(delay, MAX_BACK_OFF, ChronoUnit.SECONDS)
                .onRetry(onRetry)
                .abortOn(s -> isShuttingDown || s.getMessage().contains("Connection pool shut down"))
                .withMaxRetries(tries);
        return net.jodah.failsafe.Failsafe.with(retryPolicy);
    }

    public static <K> K execute(ContextualSupplier<K> supplier, int tries, int delay, Class<? extends Throwable> exception,
            CheckedConsumer<? extends ExecutionAttemptedEvent<Object>> onRetry) {
        return getExecutor(tries, delay, exception, onRetry).get(supplier);
    }


    public static void execute(CheckedRunnable runnable, int tries, int delay, Class<? extends Throwable> exception,
            CheckedConsumer<? extends ExecutionAttemptedEvent<Object>> onRetry) {
        getExecutor(tries, delay, exception, onRetry).run(runnable);
    }

    public static <K> K execute(ContextualSupplier<K> supplier, int tries,
            CheckedConsumer<? extends ExecutionAttemptedEvent<Object>> onRetry) {
        return execute(supplier, tries, DEFAULT_RETRY_RATE, DEFAULT_EXCEPTION, onRetry);
    }

    public static void execute(CheckedRunnable runnable, int tries,
            CheckedConsumer<? extends ExecutionAttemptedEvent<Object>> onRetry) {
        execute(runnable, tries, DEFAULT_RETRY_RATE, DEFAULT_EXCEPTION, onRetry);
    }

    public static <K> K execute(ContextualSupplier<K> supplier, int tries) {
        return execute(supplier, tries, DEFAULT_RETRY_RATE, DEFAULT_EXCEPTION, DEFAULT_ON_RETRY);
    }


    public static void execute(CheckedRunnable runnable, int tries) {
        execute(runnable, tries, DEFAULT_RETRY_RATE, DEFAULT_EXCEPTION, DEFAULT_ON_RETRY);
    }

    public static <K> K execute(ContextualSupplier<K> supplier, CheckedConsumer<? extends ExecutionAttemptedEvent<Object>> onRetry) {
        return execute(supplier, DEFAULT_RETRIES, DEFAULT_RETRY_RATE, DEFAULT_EXCEPTION, onRetry);
    }

    public static <K> K execute(ContextualSupplier<K> supplier) {
        return execute(supplier, DEFAULT_RETRIES, DEFAULT_RETRY_RATE, DEFAULT_EXCEPTION, DEFAULT_ON_RETRY);
    }

    public static void execute(CheckedRunnable runnable) {
        execute(runnable, DEFAULT_RETRIES, DEFAULT_RETRY_RATE, DEFAULT_EXCEPTION, DEFAULT_ON_RETRY);
    }

    public static void shutDown() {
        isShuttingDown = true;
    }

}

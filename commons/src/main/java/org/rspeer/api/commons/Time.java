package org.rspeer.api.commons;

import java.time.Duration;
import java.util.function.BooleanSupplier;

public final class Time {

    private static long defaultThreshold = 50;

    private Time() {
        throw new IllegalAccessError();
    }

    /**
     * Sleep for specified amount of milliseconds
     *
     * @param millis the amount of milliseconds you would like to sleep for
     */
    public static void sleep(int millis) {
        sleep((long) millis);
    }

    /**
     * Sleep for specified amount of milliseconds
     *
     * @param millis the amount of milliseconds you would like to sleep for
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {

        }
    }

    /**
     * This sleep will sleep until the passed condition is true for your amount of duration.
     * Useful for scenarios where you want to make sure a condition holds for a specific amount of
     * time before stopping sleeping.
     * <p>
     * Calls {@link Time#sleepUntil(BooleanSupplier, BooleanSupplier, long, long)} with a threshold of 50
     *
     * @param condition the condition which has to validate for this method to stop sleeping
     * @param duration  the duartion for which the condition has to validate for for this method to stop sleeping
     * @param timeout   the timeout after which this method will stop sleeping definitely.
     * @return true if the condition was validated for timeout ms before the timeout was reached.
     */
    public static boolean sleepUntilForDuration(BooleanSupplier condition, long duration, long timeout) {
        return sleepUntilForDuration(condition, duration, defaultThreshold, timeout);
    }

    /**
     * This sleep will sleep until the passed condition is true for your amount of duration.
     * Useful for scenarios where you want to make sure a condition holds for a specific amount of
     * time before stopping sleeping.
     *
     * @param condition the condition which has to validate for this method to stop sleeping
     * @param duration  the duartion for which the condition has to validate for for this method to stop sleeping
     * @param timeout   the timeout after which this method will stop sleeping definitely.
     * @param threshold the amount of milliseconds you would like to sleep each check
     * @return true if the condition was validated for timeout ms before the timeout was reached.
     */
    public static boolean sleepUntilForDuration(BooleanSupplier condition, long duration, long threshold, long timeout) {
        long start = System.currentTimeMillis();
        Long trueTime = null;
        long now;
        do {
            now = System.currentTimeMillis();
            if (Thread.interrupted()) {
                break;
            } else if (condition.getAsBoolean()) {
                if (trueTime == null) trueTime = now;
                long diff = now - trueTime;
                if (diff > duration) {
                    return true;
                }
            } else {
                trueTime = null;
            }

            Time.sleep(threshold);
        } while (now - start < timeout);
        return false;
    }

    /**
     * Sleep for a random amount of milliseconds between min and max
     *
     * @param min the min amount of milliseconds to sleep for
     * @param max the max amount of milliseconds to sleep for
     */
    public static void sleep(int min, int max) {
        sleep((long) min, (long) max);
    }

    /**
     * Sleep for a random amount of milliseconds between min and max
     *
     * @param min the min amount of milliseconds to sleep for
     * @param max the max amount of milliseconds to sleep for
     */
    public static void sleep(long min, long max) {
        sleep(Random.nextLong(min, max));
    }

    /**
     * Sleep until a condition is true. This is preferred to be used over static sleeps as with the dynamic
     * nature of the game, sometimes you need to sleep for longer and other times you will need to sleep
     * for a shorter amount of time.
     *
     * @param condition the condition which is validated to check if the bot needs to stop sleeping.
     * @param threshold the amount of milliseconds to sleep each iteration.
     * @param timeout   the amount of milliseconds after which the bot will stop sleeping regardless of the condition
     * @return true if the condition was validated before the timeout was reached.
     */
    public static boolean sleepUntil(BooleanSupplier condition, int threshold, long timeout) {
        return sleepUntil(condition, (long) threshold, timeout);
    }

    /**
     * Sleep until a condition is true. This is preferred to be used over static sleeps as with the dynamic
     * nature of the game, sometimes you need to sleep for longer and other times you will need to sleep
     * for a shorter amount of time.
     *
     * @param condition the condition which is validated to check if the bot needs to stop sleeping.
     * @param threshold the amount of milliseconds to sleep each iteration.
     * @param timeout   the amount of milliseconds after which the bot will stop sleeping regardless of the condition
     * @return true if the condition was validated before the timeout was reached.
     */
    public static boolean sleepUntil(BooleanSupplier condition, long threshold, long timeout) {
        return sleepUntil(condition, () -> false, threshold, timeout);
    }

    /**
     * Sleep until a condition is true. This is preferred to be used over static sleeps as with the dynamic
     * nature of the game, sometimes you need to sleep for longer and other times you will need to sleep
     * for a shorter amount of time.
     * <p>
     * Calls {@link Time#sleepUntil(BooleanSupplier, long, long)} with a threshold of 50.
     *
     * @param condition the condition which is validated to check if the bot needs to stop sleeping.
     * @param timeout   the amount of milliseconds after which the bot will stop sleeping regardless of the condition
     * @return true if the condition was validated before the timeout was reached.
     */
    public static boolean sleepUntil(BooleanSupplier condition, long timeout) {
        return sleepUntil(condition, 50, timeout);
    }

    /**
     * Sleep until a condition is true. This is preferred to be used over static sleeps as with the dynamic
     * nature of the game, sometimes you need to sleep for longer and other times you will need to sleep
     * for a shorter amount of time. You can pass a reset condition to reset the timeout timer.
     *
     * @param condition the condition which is validated to check if the bot needs to stop sleeping.
     * @param reset     the condition which, if validated, will reset the timeout timer.
     * @param threshold the amount of milliseconds to sleep each iteration.
     * @param timeout   the amount of milliseconds after which the bot will stop sleeping regardless of the condition
     * @return true if the condition was validated before the timeout was reached.
     */
    public static boolean sleepUntil(BooleanSupplier condition, BooleanSupplier reset, long threshold, long timeout) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeout) {
            if (Thread.interrupted()) {
                return false;
            } else if (condition.getAsBoolean()) {
                return true;
            }

            if (reset.getAsBoolean()) {
                start = System.currentTimeMillis();
            }

            Time.sleep(threshold);
        }
        return false;
    }

    /**
     * Sleep until a condition is true. This is preferred to be used over static sleeps as with the dynamic
     * nature of the game, sometimes you need to sleep for longer and other times you will need to sleep
     * for a shorter amount of time. You can pass a reset condition to reset the timeout timer.
     * <p>
     * Calls {@link Time#sleepUntil(BooleanSupplier, BooleanSupplier, long, long)} with a threshold of 50.
     *
     * @param condition the condition which is validated to check if the bot needs to stop sleeping.
     * @param reset     the condition which, if validated, will reset the timeout timer.
     * @param timeout   the amount of milliseconds after which the bot will stop sleeping regardless of the condition
     * @return true if the condition was validated before the timeout was reached.
     */
    public static boolean sleepUntil(BooleanSupplier condition, BooleanSupplier reset, long timeout) {
        return sleepUntil(condition, reset, defaultThreshold, timeout);
    }

    /**
     * Sleep as long as a condition is true or until timeout is reached. This method basically calls
     * {@link Time#sleepUntil(BooleanSupplier, long, long)} with the negated condition.
     *
     * @param condition the condition that needs to be true for the sleep to continue
     * @param threshold the amount of milliseconds to sleep each loop for
     * @param timeout   The timeout after which sleeping is stopped
     * @return true if the condition was invalidated before the timeout was reached
     */
    public static boolean sleepWhile(BooleanSupplier condition, int threshold, long timeout) {
        return sleepUntil(() -> !condition.getAsBoolean(), threshold, timeout);
    }

    /**
     * Sleep as long as a condition is true or until timeout is reached. This method basically calls
     * {@link Time#sleepUntil(BooleanSupplier, long, long)} with the negated condition. Uses the default
     * threshold value of 50 milliseconds.
     *
     * @param condition the condition that needs to be true for the sleep to continue
     * @param timeout   The timeout after which sleeping is stopped
     * @return true if the condition was invalidated before the timeout was reached
     */
    public static boolean sleepWhile(BooleanSupplier condition, long timeout) {
        return sleepUntil(() -> !condition.getAsBoolean(), defaultThreshold, timeout);
    }

    public static String format(Duration duration) {
        long secs = Math.abs(duration.getSeconds());
        return String.format("%02d:%02d:%02d", secs / 3600, (secs % 3600) / 60, secs % 60);
    }

    public static long getDefaultThreshold() {
        return defaultThreshold;
    }

    public static void setDefaultThreshold(long defaultThreshold) {
        Time.defaultThreshold = defaultThreshold;
    }
}

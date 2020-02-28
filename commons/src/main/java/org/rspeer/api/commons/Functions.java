package org.rspeer.api.commons;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public final class Functions {

    private Functions() {
        throw new IllegalAccessError();
    }

    /**
     * @param supplier The supplier
     * @param function The function to apply
     * @param fallback The value to return if the supplied arg is null
     * @param <K>      argument type
     * @param <R>      return type
     * @return Applies a single argument function to the supplied argument
     * and returns the result if the arg is not null, else returns the fallback value
     */
    public static <K, R> R mapOrDefault(Supplier<K> supplier, Function<K, R> function, R fallback) {
        K value = supplier.get();
        return value != null ? function.apply(value) : fallback;
    }

    public static <K, R> R mapOrNull(Supplier<K> supplier, Function<K, R> function) {
        return mapOrDefault(supplier, function, null);
    }

    public static <K> boolean mapOrElse(Supplier<K> supplier, ToBooleanFunction<K> function, boolean fallback) {
        K value = supplier.get();
        return value != null ? function.applyAsBoolean(value) : fallback;
    }

    public static <K> boolean mapOrElse(Supplier<K> supplier, ToBooleanFunction<K> function) {
        return mapOrElse(supplier, function, false);
    }

    public static <K> int mapOrDefault(Supplier<K> supplier, ToIntFunction<K> function, int fallback) {
        K value = supplier.get();
        return value != null ? function.applyAsInt(value) : fallback;
    }

    public static <K> int mapOrM1(Supplier<K> supplier, ToIntFunction<K> function) {
        return mapOrDefault(supplier, function, -1);
    }

    public static <K> void ifPresent(Supplier<K> supplier, Consumer<K> consumer) {
        K value = supplier.get();
        if (value != null) {
            consumer.accept(value);
        }
    }
}

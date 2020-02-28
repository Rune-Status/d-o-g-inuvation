package org.rspeer.api.commons;

import java.util.Objects;

/**
 * An expression which evaluates to a boolean
 */
public interface Procedure {

    /**
     * @return {@code true} if this procedure was successfully executed
     */
    boolean invoke();

    /**
     * Executes this procedure and then another.
     *
     * @param after The Procedure to invoke following this one
     * @return A Procedure which returns {@code true} if both procedures successfully executed (i.e. returned true)
     */
    default Procedure andThen(Procedure after) {
        Objects.requireNonNull(after);
        return () -> {
            boolean first = invoke();
            boolean next = after.invoke();
            return first && next;
        };
    }

    /**
     * Executes this procedure and then another, but only IF this procedure was successful.
     *
     * @param after The Procedure to invoke following this one
     * @return {@code true} if both procedures successfully executed
     */
    default boolean ifThen(Procedure after) {
        return invoke() && after.invoke();
    }
}

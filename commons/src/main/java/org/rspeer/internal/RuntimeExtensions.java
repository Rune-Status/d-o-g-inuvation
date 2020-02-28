package org.rspeer.internal;

public class RuntimeExtensions {

    /**
     * Forces a hard shutdown and doesn't try to close gracefully.
     */
    public static void shutdown() {
        Runtime.getRuntime().halt(0);
    }

}

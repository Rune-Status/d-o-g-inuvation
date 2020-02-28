package org.rspeer.api.concurrent;

import net.jodah.failsafe.function.CheckedRunnable;

/**
 * Created by Spencer on 18/09/2018.
 */
public interface Task extends CheckedRunnable {

    default String verbose() {
        return "";
    }
}

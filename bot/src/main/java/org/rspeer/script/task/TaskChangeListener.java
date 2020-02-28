package org.rspeer.script.task;

/**
 * Created by Spencer on 05/11/2018.
 */
public interface TaskChangeListener {
    void notify(Task prev, Task curr);
}

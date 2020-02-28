package org.rspeer.script.task;

import org.rspeer.script.ScriptController;

public abstract class Task {

    public abstract int execute();

    public abstract boolean validate();

    public String getStatus() {
        return getClass().getSimpleName();
    }

    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    public ScriptController getController() {
        return ScriptController.getInstance();
    }
}

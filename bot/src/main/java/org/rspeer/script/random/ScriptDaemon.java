package org.rspeer.script.random;

import org.rspeer.script.Script;
import org.rspeer.script.task.Task;

public abstract class ScriptDaemon extends Task {

    protected Script script;

    public ScriptDaemon(Script script) {
        this.script = script;
    }

    public abstract String name();

    public void reset() {

    }
}

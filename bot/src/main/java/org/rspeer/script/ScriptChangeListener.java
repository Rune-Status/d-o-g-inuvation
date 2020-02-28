package org.rspeer.script;

public interface ScriptChangeListener {
    void notify(Script previous, Script current);
}

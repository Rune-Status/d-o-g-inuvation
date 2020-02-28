package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.RuneScriptEvent;

public interface RuneScriptListener extends EventListener {
    void notify(RuneScriptEvent e);
}

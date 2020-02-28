package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.EngineTickEvent;

public interface EngineTickListener extends EventListener {
    void notify(EngineTickEvent e);
}

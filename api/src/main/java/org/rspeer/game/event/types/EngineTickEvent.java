package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.event.listener.EngineTickListener;
import org.rspeer.game.event.listener.EventListener;

public final class EngineTickEvent extends Event<String> {

    public EngineTickEvent() {
        super("Static", "Static");
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof EngineTickListener) {
            ((EngineTickListener) listener).notify(this);
        }
    }
}

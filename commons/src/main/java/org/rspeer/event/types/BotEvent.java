package org.rspeer.event.types;

import org.rspeer.api.collections.Pair;
import org.rspeer.event.Event;
import org.rspeer.event.listeners.BotEventListener;
import org.rspeer.game.event.listener.EventListener;

public final class BotEvent extends Event<Pair<String, Object>> {

    public BotEvent(Pair<String, Object> source) {
        super(source, "Static");
    }

    public BotEvent(String key, Object value) {
        super(new Pair<>(key, value), "Static");
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof BotEventListener) {
            ((BotEventListener) listener).notify(this);
        }
    }
}

package org.rspeer.event;

import org.rspeer.game.event.listener.EventListener;

import java.util.EventObject;

public abstract class Event<K> extends EventObject {

    private final String context;
    private final long time;

    public Event(K source, String context) {
        this(source, context, 0);
    }

    public Event(K source, String context, long delay) {
        super(source);
        this.context = context;
        time = System.currentTimeMillis() + delay;
    }

    @Override
    public K getSource() {
        return (K) source;
    }

    public long getTime() {
        return time;
    }

    public String getContext() {
        return context;
    }

    public abstract void forward(EventListener listener);
}

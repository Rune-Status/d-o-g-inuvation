package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.event.listener.ConnectionStateListener;
import org.rspeer.game.event.listener.EventListener;

public final class ConnectionStateEvent extends Event<String> {

    private final int previous, current;

    public ConnectionStateEvent(int previous, int current, String context) {
        super("Static", context);
        this.previous = previous;
        this.current = current;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ConnectionStateListener) {
            ((ConnectionStateListener) listener).notify(this);
        }
    }

    public int getPrevious() {
        return previous;
    }

    public int getCurrent() {
        return current;
    }
}

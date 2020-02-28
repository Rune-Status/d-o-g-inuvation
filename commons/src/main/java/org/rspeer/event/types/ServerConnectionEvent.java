package org.rspeer.event.types;

import org.rspeer.event.Event;
import org.rspeer.event.listeners.ServerConnectionChangeListener;
import org.rspeer.game.event.listener.EventListener;

public final class ServerConnectionEvent extends Event<ServerConnectionEvent.Status> {

    public ServerConnectionEvent(Status status) {
        super(status, "Static");
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof ServerConnectionChangeListener) {
            ((ServerConnectionChangeListener) listener).notify(this);
        }
    }

    public enum Status {
        CONNECTED, DISCONNECTED
    }
}

package org.rspeer.event.types;

import org.rspeer.event.Event;
import org.rspeer.event.listeners.RemoteMessageListener;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.networking.entities.RemoteMessage;

public final class RemoteMessageEvent extends Event<RemoteMessage> {

    public RemoteMessageEvent(RemoteMessage source) {
        super(source, "Static");
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof RemoteMessageListener) {
            ((RemoteMessageListener) listener).notify(this);
        }
    }
}

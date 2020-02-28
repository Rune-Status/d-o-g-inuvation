package org.rspeer.event.listeners;

import org.rspeer.event.types.ServerConnectionEvent;
import org.rspeer.game.event.listener.EventListener;

public interface ServerConnectionChangeListener extends EventListener {
    void notify(ServerConnectionEvent e);
}

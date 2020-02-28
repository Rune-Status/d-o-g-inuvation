package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.ConnectionStateEvent;

public interface ConnectionStateListener extends EventListener {
    void notify(ConnectionStateEvent e);
}

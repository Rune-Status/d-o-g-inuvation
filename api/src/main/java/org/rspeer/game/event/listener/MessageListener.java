package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.MessageEvent;

public interface MessageListener extends EventListener {
    void notify(MessageEvent e);
}

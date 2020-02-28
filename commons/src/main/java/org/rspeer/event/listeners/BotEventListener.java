package org.rspeer.event.listeners;

import org.rspeer.event.types.BotEvent;
import org.rspeer.game.event.listener.EventListener;

public interface BotEventListener extends EventListener {
    void notify(BotEvent e);
}

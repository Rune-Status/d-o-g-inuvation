package org.rspeer.event.listeners;

import org.rspeer.event.types.RemoteMessageEvent;
import org.rspeer.game.event.listener.EventListener;

public interface RemoteMessageListener extends EventListener {
    void notify(RemoteMessageEvent e);
}

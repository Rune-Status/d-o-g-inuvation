package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.EntityHoverEvent;

public interface EntityHoverListener extends EventListener {
    void notify(EntityHoverEvent e);
}

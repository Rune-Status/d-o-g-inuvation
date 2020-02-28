package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.TableItemEvent;

public interface TableItemListener extends EventListener {
    void notify(TableItemEvent e);
}

package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.MenuActionEvent;

public interface MenuActionListener extends EventListener {
    void notify(MenuActionEvent e);
}

package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.LoginResponseEvent;

public interface LoginResponseListener {
    void notify(LoginResponseEvent e);
}

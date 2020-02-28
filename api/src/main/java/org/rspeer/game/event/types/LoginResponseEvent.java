package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.event.listener.LoginResponseListener;

public final class LoginResponseEvent extends Event<LoginResponseEvent.Type> {

    public static final int PROCESSING = -3;
    public static final int SUCCESS = 2;
    public static final int INVALID = 3;
    public static final int BANNED = 4;
    public static final int ALREADY_LOGGED_IN = 5;

    private final int previous;
    private final int current;

    public LoginResponseEvent(Type source, String context, int previous, int current) {
        super(source, context);
        this.previous = previous;
        this.current = current;
    }

    public int getPrevious() {
        return previous;
    }

    public int getCurrent() {
        return current;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof LoginResponseListener) {
            ((LoginResponseListener) listener).notify(this);
        }
    }

    public enum Type {
        LOGIN,
        LOBBY;
    }
}

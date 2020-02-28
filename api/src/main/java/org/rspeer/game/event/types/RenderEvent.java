package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.event.listener.RenderListener;

import java.awt.*;

public final class RenderEvent extends Event<Graphics> {

    public RenderEvent(Graphics source, String context) {
        super(source, context);
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof RenderListener) {
            ((RenderListener) listener).notify(this);
        }
    }
}

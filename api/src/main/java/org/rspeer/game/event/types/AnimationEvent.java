package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.adapter.scene.Mobile;
import org.rspeer.game.event.listener.AnimationListener;
import org.rspeer.game.event.listener.EventListener;

public final class AnimationEvent extends Event<Mobile> {

    private int previous, current;

    public AnimationEvent(Mobile source, String context, int previous, int current) {
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
        if (listener instanceof AnimationListener) {
            ((AnimationListener) listener).notify(this);
        }
    }
}

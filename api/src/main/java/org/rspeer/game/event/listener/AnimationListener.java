package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.AnimationEvent;

public interface AnimationListener extends EventListener {
    void notify(AnimationEvent event);
}

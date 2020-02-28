package org.rspeer.game.event.listener;

import org.rspeer.game.event.types.AnimationChangedEvent;
import org.rspeer.game.event.types.AnimationEvent;

@Deprecated
public interface AnimationChangedListener extends EventListener {
    void notify(AnimationChangedEvent event);
}

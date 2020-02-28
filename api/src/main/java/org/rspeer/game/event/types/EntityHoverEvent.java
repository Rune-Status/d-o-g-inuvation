package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.event.listener.EntityHoverListener;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.providers.RSSceneNode;

import java.awt.*;

public final class EntityHoverEvent extends Event<RSSceneNode> {

    private final Point location;

    public EntityHoverEvent(RSSceneNode source, Point location) {
        super(source, "Static");
        this.location = location;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof EntityHoverListener) {
            ((EntityHoverListener) listener).notify(this);
        }
    }

    public Point getLocation() {
        return location;
    }
}

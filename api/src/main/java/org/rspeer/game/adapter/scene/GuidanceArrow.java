package org.rspeer.game.adapter.scene;

import org.rspeer.api.commons.Identifiable;
import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.api.position.*;
import org.rspeer.game.providers.RSGuidanceArrow;

public final class GuidanceArrow extends Adapter<RSGuidanceArrow> implements Identifiable, Positionable {

    public GuidanceArrow(RSGuidanceArrow provider) {
        super(provider);
    }

    @Override
    public ScenePosition getScenePosition() {
        return getAbsolutePosition().getScenePosition();
    }

    @Override
    public WorldPosition getPosition() {
        return getAbsolutePosition().getPosition();
    }

    @Override
    public FinePosition getAbsolutePosition() {
        return Position.absolute(provider.getX(), provider.getY(), provider.getFloorLevel());
    }

    public int getTargetIndex() {
        return provider.getTargetIndex();
    }

    public int getId() {
        return provider.getId();
    }

    public int getType() {
        return provider.getType();
    }
}

package org.rspeer.game.adapter.scene;

import org.rspeer.game.adapter.Adapter;
import org.rspeer.game.providers.RSCoordinateSpace;
import org.rspeer.game.providers.RSSceneEntity;

public abstract class SceneEntity<K extends RSSceneEntity> extends Adapter<K> implements RSSceneEntity {

    protected SceneEntity(K provider) {
        super(provider);
    }

    @Override
    public short getEndY() {
        return provider.getEndY();
    }

    @Override
    public short getEndX() {
        return provider.getEndX();
    }

    @Override
    public short getStartY() {
        return provider.getStartY();
    }

    @Override
    public short getStartX() {
        return provider.getStartX();
    }

    @Override
    public byte getFloorLevel() {
        return provider.getFloorLevel();
    }

    @Override
    public int getHeight() {
        return provider.getHeight();
    }

    @Override
    public RSCoordinateSpace getPositionSpace() {
        return provider.getPositionSpace();
    }

    @Override
    public RSCoordinateSpace getPos() {
        return provider.getPos();
    }
}

package org.rspeer.game.providers;

public interface RSDynamicGameObject extends RSSceneEntity {

    byte getOrientation();

    RSModel getModel();

    int getId();

    byte getType();

    void setOrientation(int orientation);
}
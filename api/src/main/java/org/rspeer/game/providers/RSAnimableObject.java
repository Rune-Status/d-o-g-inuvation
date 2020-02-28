package org.rspeer.game.providers;

public interface RSAnimableObject extends RSSceneEntity {

    RSParticleProvider getParticleProvider();

    int getGraphic();

    RSAnimator getAnimator();

    default int getId() {
        return getGraphic();
    }
}
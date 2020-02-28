package org.rspeer.game.providers;

import org.rspeer.api.commons.Functions;

public interface RSAnimator extends RSProvider {

    int getAnimationFrame();

    RSAnimation getAnimation();

    RSMobile getOwner();

    default int getAnimationId() {
        return Functions.mapOrM1(this::getAnimation, RSAnimation::getId);
    }
}
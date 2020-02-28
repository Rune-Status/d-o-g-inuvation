package org.rspeer.game.providers;

public interface RSCoordinateSpace extends RSProvider {

    RSQuaternion getRotation();

    RSVector3f getTranslation();
}
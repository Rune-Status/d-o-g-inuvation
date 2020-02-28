package org.rspeer.game.providers;

public interface RSAnimation extends RSProvider {

    int[] getFrameDurations();

    int getId();

    RSNodeTable getParameters();
}
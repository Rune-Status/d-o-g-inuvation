package org.rspeer.game.providers;

public interface RSDirectXRenderConfiguration extends RSRenderConfiguration {

    float getMultiplierY();

    float getAbsoluteX();

    float getAbsoluteY();

    RSMatrix4f getMatrix4f();

    float getMultiplierX();
}
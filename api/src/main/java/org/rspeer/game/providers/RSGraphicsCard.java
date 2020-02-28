package org.rspeer.game.providers;

public interface RSGraphicsCard extends RSProvider {

    long getDriver();

    String getName();

    int getVendorId();

    int getVersion();

    String getDevice();
}
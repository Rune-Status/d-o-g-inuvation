package org.rspeer.game.providers;

public interface RSArchive extends RSProvider {

    int getDiscardUnpacked();

    Object[][] getUnpacked();

    RSResourceProvider getResourceProvider();

    boolean isDiscardPacked();

    Object[] getPacked();

    RSReferenceTable getTable();
}
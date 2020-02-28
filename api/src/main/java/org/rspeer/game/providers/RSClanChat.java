package org.rspeer.game.providers;

public interface RSClanChat extends RSProvider {

    int getMemberCount();

    int getOwnerIndex();

    long getHash();

    RSNodeTable getParameters();

    byte[] getMemberRanks();

    int[] getMemberIndices();

    int[] getVarps();

    String[] getMemberNames();

    String getChannelName();
}

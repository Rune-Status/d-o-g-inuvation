package org.rspeer.game.providers;

public interface RSBuffer extends RSNode {

    int getCaret();

    byte[] getPayload();
}
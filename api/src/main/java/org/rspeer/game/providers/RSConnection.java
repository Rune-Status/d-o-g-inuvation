package org.rspeer.game.providers;

public interface RSConnection extends RSProvider {
    void finalize(); //kill connection
}
package org.rspeer.game.event.callback;

public interface CommandProcessor {
    boolean accept(String cmd);
}

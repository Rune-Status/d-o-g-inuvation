package org.rspeer.game.providers;

public interface RSFrameBuffer extends RSBuffer {

    RSIsaacCipher getCipher();

    int getBitCaret();
}
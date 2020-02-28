package org.rspeer.game.providers;

import java.io.IOException;
import java.io.OutputStream;

public interface RSAsyncOutputStream extends RSProvider {

    IOException getException();

    boolean isStopped();

    byte[] getBuffer();

    Thread getThread();

    OutputStream getTarget();
}
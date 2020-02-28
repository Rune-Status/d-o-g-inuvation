package org.rspeer.game.providers;

import java.io.IOException;
import java.io.InputStream;

public interface RSAsyncInputStream extends RSProvider {

    IOException getException();

    byte[] getBuffer();

    Thread getThread();

    InputStream getTarget();
}
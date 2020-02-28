package org.rspeer.game.providers;

import java.net.Socket;

public interface RSAsyncConnection extends RSConnection {

    RSAsyncOutputStream getOutput();

    RSAsyncInputStream getInput();

    Socket getSocket();
}
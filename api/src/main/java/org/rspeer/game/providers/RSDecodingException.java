package org.rspeer.game.providers;

public interface RSDecodingException extends RSProvider {

    String getMessage();

    void setMessage(String message);

    Throwable getCause();
}
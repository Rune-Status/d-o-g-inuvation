package org.rspeer.game.providers;

public interface RSChatMessage extends RSDoublyNode {

    RSChatMessageIcon getIcon();

    String getSource();

    int getType();

    String getContent();
}
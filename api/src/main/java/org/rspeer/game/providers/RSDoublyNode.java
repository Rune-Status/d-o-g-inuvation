package org.rspeer.game.providers;

public interface RSDoublyNode extends RSNode {

    long getSubHash();

    RSDoublyNode getSubPrevious();

    RSDoublyNode getSubNext();
}
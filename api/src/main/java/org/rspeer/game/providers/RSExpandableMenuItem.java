package org.rspeer.game.providers;

public interface RSExpandableMenuItem extends RSDoublyNode {

    RSDoublyNodeQueue getChildrenQueue();

    String getText();

    int getChildrenCount();
}
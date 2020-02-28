package org.rspeer.game.providers;

public interface RSCache extends RSProvider {

    RSDoublyNodeQueue getReferenceQueue();

    RSNodeTable getReferenceTable();
}
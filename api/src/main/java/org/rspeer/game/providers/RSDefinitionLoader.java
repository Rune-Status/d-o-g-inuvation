package org.rspeer.game.providers;

public interface RSDefinitionLoader<K extends RSDefinition> extends RSProvider, Iterable<K> {

    K load(int id);

    int count();
}
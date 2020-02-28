package org.rspeer.game.providers;

public interface RSDefinitionCacheLoader<K extends RSDefinition> extends RSProvider {

    RSCache getCache();

    RSJs5ConfigGroup getConfigGroup();

    int getCount();

    K load(int id);
}
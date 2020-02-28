package org.rspeer.loader;

public interface GameConfiguration {

    /**
     * @return The GameSource that this configuration is for
     */
    GameSource getSource();

    /**
     * @return The location of the game archive
     */
    String getArchive();

    /**
     * @param key The parameter name
     * @return An applet parameter using the given key
     */
    String getParameter(String key);

    /**
     * Loads the game config
     */
    void load();

    /**
     * Gets the current language to load
     */
    Language getLanguage();

    /**
     * Gets the proxy to load the game from
     */
    ProxyConfig getProxy();
}

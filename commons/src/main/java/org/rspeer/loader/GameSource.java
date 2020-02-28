package org.rspeer.loader;

import org.rspeer.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Septron
 * @since September 13, 2018
 */
public enum GameSource {

    /**
     * The oldschool game.
     */
    OLDSCHOOL("oldschool.runescape.com", 170, false),

    /**
     * The runescape game.
     */
    RUNESCAPE("runescape.com", 895, true);

    private final String base;

    /**
     * Whether or not the client source code is encrypted.
     */
    private final boolean encrypted;

    /**
     * The last known revision to begin checking against.
     */
    private final int revision;

    /**
     * Creates the ClientSource.
     *
     * @param base
     * @param revision
     * @param encrypted Whether or not the client source code is encrypted.
     */
    GameSource(String base, int revision, boolean encrypted) {
        this.base = base;
        this.revision = revision;
        this.encrypted = encrypted;
    }

    public String getJavConfig(int language) {
        return "http://" + base + "/l=" + language + "/jav_config.ws";
    }

    public String getBase() {
        return base;
    }

    public String derive(int world) {
        if (this == OLDSCHOOL) {
            return base.replace("oldschool", "oldschool" + world);
        }
        return "world" + world + "." + base;
    }

    public int getRevision() {
        return revision;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public Path getFilePath() {
        return Paths.get(Configuration.CACHE, super.toString().toLowerCase() + ".jar");
    }
}

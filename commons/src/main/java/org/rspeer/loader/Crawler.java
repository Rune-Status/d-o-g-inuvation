package org.rspeer.loader;

import org.rspeer.io.Internet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarInputStream;

public final class Crawler {

    private final GameConfiguration configuration;
    private int hash;

    public Crawler(GameConfiguration configuration) {
        this.configuration = configuration;
    }

    public int getLocalHash() {
        try {
            URL url = configuration.getSource().getFilePath().toFile().toURI().toURL();
            try (JarInputStream stream = new JarInputStream(url.openStream())) {
                return stream.getManifest().hashCode();
            } catch (Exception e) {
                return -1;
            }
        } catch (MalformedURLException e) {
            return -1;
        }
    }

    public int getRemoteHash() {
        try {
            URL url = new URL(configuration.getArchive());
            try (JarInputStream stream = new JarInputStream(url.openStream())) {
                return stream.getManifest().hashCode();
            } catch (Exception e) {
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean isOutdatedLocally() {
        if (!configuration.getSource().getFilePath().toFile().exists()) {
            return true;
        } else if (hash == -1) {
            hash = getLocalHash();
        }
        return hash == -1 || hash != getRemoteHash();
    }

    public boolean downloadTo(String target) {
        hash = getRemoteHash();
        return Internet.download(configuration.getArchive(), target, true) != null;
    }

    public JarInputStream getStream() {
        try {
            return new JarInputStream(new URL(configuration.getArchive()).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean download() {
        return downloadTo(configuration.getSource().getFilePath().toString());
    }
}
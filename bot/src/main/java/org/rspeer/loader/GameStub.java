package org.rspeer.loader;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.MalformedURLException;
import java.net.URL;

public final class GameStub implements AppletStub {

    private final GameConfiguration config;

    public GameStub(GameConfiguration config) {
        this.config = config;
    }

    public boolean isActive() {
        return true;
    }

    public URL getDocumentBase() {
        return getCodeBase();
    }

    public URL getCodeBase() {
        try {
            return new URL(config.getArchive());
        } catch (MalformedURLException e) {
            try {
                return new URL("http://world69.runescape.com/gamepack.jar");
            } catch (MalformedURLException e1) {
                return null;
            }
        }
    }

    public String getParameter(String name) {
        return config.getParameter(name);
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    public void appletResize(int width, int height) {
    }
}

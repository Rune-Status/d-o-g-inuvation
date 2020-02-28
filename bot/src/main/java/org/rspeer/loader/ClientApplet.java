package org.rspeer.loader;

import org.rspeer.game.api.Game;
import org.rspeer.game.providers.RSClient;

import java.applet.Applet;
import java.awt.*;

public final class ClientApplet extends Applet {

    public static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

    private final RSClient client;

    public ClientApplet(ClassLoader classLoader) {
        try {
            Class<?> clazz = classLoader.loadClass("client");
            Game.setClient(client = (RSClient) clazz.newInstance());
            client.supplyApplet(this);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing applet", e);
        }
    }

    private static boolean initLocked = false;

    @Override
    public void init() {
        if (!initLocked) {
            client.init();
            initLocked = true;
        }
    }

    @Override
    public void start() {
        client.start();
    }
}
package org.rspeer.game.api.input;

import org.rspeer.game.api.Game;

import java.awt.*;
import java.awt.event.KeyEvent;

public final class Keyboard {

    private Keyboard() {
        throw new IllegalAccessError();
    }

    public static synchronized void sendText(String text) {
        sendText(text, false);
    }

    public static synchronized void sendText(String text, boolean pressEnter) {
        for (char c : text.toCharArray()) {
            sendKey(c);
        }

        if (pressEnter) {
            pressEnter();
        }
    }

    public static synchronized void pressEnter() {
        pressEventKey(KeyEvent.VK_ENTER);
    }

    public static synchronized void sendKey(char key) {
        dispatch(generateEvent(key, KeyEvent.KEY_TYPED));
    }

    public static synchronized void pressEventKey(int eventKey) {
        dispatch(generateEvent(eventKey, KeyEvent.KEY_PRESSED));
        dispatch(generateEvent(eventKey, KeyEvent.KEY_RELEASED));
    }

    private static KeyEvent generateEvent(char key, int event) {
        AWTKeyStroke stroke = AWTKeyStroke.getAWTKeyStroke(key);
        return new KeyEvent(getCanvas(),
                event,
                System.currentTimeMillis(), stroke.getModifiers(), stroke.getKeyCode(),
                stroke.getKeyChar());
    }

    public static KeyEvent generateEvent(int key, int event) {
        return new KeyEvent(getCanvas(), event, System.currentTimeMillis(), 0, key,
                (char) key, KeyEvent.KEY_LOCATION_STANDARD);
    }

    public static void dispatch(KeyEvent e) {
        getCanvas().dispatchEvent(e);
    }

    private static Canvas getCanvas() {
        return Game.getClient().getCanvas();
    }

    public static synchronized void pushKey(int eventKey) {
        dispatch(generateEvent(eventKey, KeyEvent.KEY_PRESSED));
    }

    public static synchronized void releaseKey(int eventKey) {
        dispatch(generateEvent(eventKey, KeyEvent.KEY_RELEASED));
    }
}

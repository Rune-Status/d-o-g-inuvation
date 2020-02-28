package org.rspeer.game.api.input;

import org.rspeer.api.commons.Random;
import org.rspeer.game.api.Game;

import java.awt.*;
import java.awt.event.MouseEvent;

@Deprecated
public final class Mouse {

    private static int mouseX, mouseY;

    private Mouse() {
        throw new IllegalAccessError();
    }

    public static void move(int x, int y) {
        dispatch(generateMouseEvent(MouseEvent.MOUSE_MOVED, mouseX = x, mouseY = y, MouseEvent.NOBUTTON));
    }

    public static void click(boolean left, int x, int y) {
        move(x, y);
        click(left);
    }

    public static void click(int x, int y) {
        click(true, x, y);
    }

    public static void click(Point p) {
        click(true, p.x, p.y);
    }

    public static void click(boolean left) {
        press(left);
        release(left);
    }

    public static void press(boolean left) {
        dispatch(generateMouseEvent(MouseEvent.MOUSE_PRESSED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3));
    }

    public static void release(boolean left) {
        int offset = Random.nextInt(20, 30);
        dispatch(generateMouseEvent(MouseEvent.MOUSE_RELEASED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
        dispatch(generateMouseEvent(MouseEvent.MOUSE_CLICKED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
    }

    private static MouseEvent generateMouseEvent(int type, int x, int y, int button, int timeOffset) {
        return new MouseEvent(getCanvas(), type, System.currentTimeMillis() + timeOffset, 0, x, y,
                button != MouseEvent.MOUSE_MOVED ? 1 : 0, false, button);
    }

    private static MouseEvent generateMouseEvent(int type, int x, int y, int button) {
        return generateMouseEvent(type, x, y, button, 0);
    }

    public static void dispatch(MouseEvent e) {
        getCanvas().dispatchEvent(e);
    }

    private static Canvas getCanvas() {
        return Game.getClient().getCanvas();
    }
}

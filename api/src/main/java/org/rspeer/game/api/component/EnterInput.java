package org.rspeer.game.api.component;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.input.Keyboard;

import java.awt.event.KeyEvent;

public final class EnterInput {

    private static final int GROUP_INDEX = InterfaceComposite.ENTER_AMOUNT.getGroup();

    private static final InterfaceAddress INPUT_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(GROUP_INDEX, a -> a.getType() == 4 && a.getHeight() == 20)
    );

    private EnterInput() {
        throw new IllegalAccessError();
    }

    public static boolean isOpen() {
        return INPUT_ADDRESS.mapToBoolean(InterfaceComponent::isVisible);
    }

    public static boolean isClosed() { //just for easy use with method references
        return !isOpen();
    }

    public static String getEntry() {
        return INPUT_ADDRESS.map(InterfaceComponent::getText);
    }

    public static int getNumericEntry() {
        try {
            return Integer.parseInt(getEntry());
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean initiate(String amount) {
        if (!isOpen()) {
            return false;
        }

        String inputText = getEntry();
        if (!inputText.equals(amount) && inputText.trim().length() > 0) {
            for (int i = 0; i < inputText.length(); i++) {
                Keyboard.pressEventKey(KeyEvent.VK_BACK_SPACE);
            }
        }

        if (!inputText.equals(amount)) {
            Keyboard.sendText(String.valueOf(amount));
        }

        Keyboard.pressEnter();
        return Time.sleepUntil(EnterInput::isClosed, 1200);
    }

    public static boolean initiate(int amount) {
        return initiate(String.valueOf(amount));
    }
}

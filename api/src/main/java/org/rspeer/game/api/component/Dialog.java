package org.rspeer.game.api.component;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Game;

import java.util.function.Predicate;

public final class Dialog {

    private static final int OPTION_GROUP_INDEX = InterfaceComposite.CHAT_DIALOG_OPTIONS.getGroup();

    private final boolean ignoreOrder;
    private final int[] indexOrder;

    private int currentIndex;

    public Dialog(boolean ignoreOrder, int... indexOrder) {
        this.ignoreOrder = ignoreOrder;
        this.indexOrder = indexOrder;
    }

    private static int indexOf(Predicate<String> predicate) {
        if (!Dialog.isViewingChatOptions()) {
            return -1;
        }

        int actionCompIndex = 0;
        for (InterfaceComponent comp : Interfaces.getComponents(OPTION_GROUP_INDEX)) {
            String text = comp.getText();
            if (text != null && !text.trim().equals("") && !text.matches("-?\\d+(\\.\\d+)?.")) {
                actionCompIndex++;
                if (predicate.test(text)) {
                    return actionCompIndex;
                }
            }
        }

        return -1;
    }

    public static boolean process(Predicate<String> predicate) {
        if (isProcessing()) {
            return false;
        }

        int index = indexOf(predicate);
        if (index == -1) {
            return false;
        }

        int temp = 0;
        for (InterfaceComponent comp : Interfaces.getComponents(OPTION_GROUP_INDEX)) {
            if (comp.containsAction("Continue") && ++temp == index && comp.interact("Continue")) {
                return true;
            }
        }
        return false;
    }

    public static boolean process(String text) {
        return process(a -> a.equals(text));
    }

    public static boolean process(String... texts) {
        for (String t : texts) {
            if (process(t)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isProcessing() {
        return Game.getClient().getDialogProcessingComponent() != null;
    }

    public static boolean processContinue() {
        if (isViewingChatOptions() || isProcessing()) {
            return false;
        }

        InterfaceComponent component = Interfaces.getFirst(e -> e.containsAction("Continue") && e.isVisible());
        return component != null && component.interact("Continue");
    }

    public static boolean canContinue() {
        return !isViewingChatOptions() && Interfaces.getFirst(e -> e.containsAction("Continue") && e.isVisible()) != null;
    }

    public static boolean isViewingChatOptions() {
        return Interfaces.isOpen(OPTION_GROUP_INDEX);
    }

    public static boolean isOpen() {
        return Interfaces.isOpen(1188) || Interfaces.isOpen(1184)
                || Interfaces.isOpen(1189) || Interfaces.isOpen(1186)
                || Interfaces.isOpen(1191);
    }

    public boolean process() {
        if (isProcessing()) {
            return false;
        }

        if (isViewingChatOptions()) {
            int actionCompIndex = 0;
            for (InterfaceComponent comp : Interfaces.getComponents(OPTION_GROUP_INDEX)) {
                String text = comp.getText();
                if (text != null && text.matches("-?\\d+(\\.\\d+)?.")) {
                    actionCompIndex++;
                    if (text.equalsIgnoreCase(String.valueOf(indexOrder[currentIndex]) + ".")) {
                        break;
                    }
                }
            }
            int temp = 0;
            for (InterfaceComponent comp : Interfaces.getComponents(OPTION_GROUP_INDEX)) {
                if (comp.containsAction("Continue")) {
                    temp++;
                    if (temp == actionCompIndex) {
                        comp.interact("Continue");
                        if (Time.sleepUntil(() -> !isViewingChatOptions(), 2000)) {
                            Time.sleep(750);
                            currentIndex++;
                            return true;
                        }
                    }
                }
            }
        } else if (isOpen()) {
            processContinue();
        }
        return false;
    }

    public void reset() {
        currentIndex = 0;
    }

    public boolean isComplete() {
        return indexOrder.length <= currentIndex;
    }
}

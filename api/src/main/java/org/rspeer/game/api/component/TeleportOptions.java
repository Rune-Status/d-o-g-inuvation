package org.rspeer.game.api.component;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.action.ActionOpcodes;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.tree.Action;

import java.util.function.Predicate;

public final class TeleportOptions {

    private static final int INTERFACE_INDEX = InterfaceComposite.TELEPORT_OPTIONS.getGroup();

    private static final InterfaceAddress CLOSE
            = new InterfaceAddress(() -> Interfaces.getFirst(INTERFACE_INDEX, x -> x.containsAction("Close")));

    private TeleportOptions() {
        throw new IllegalAccessError();
    }

    public static boolean isOpen() {
        return Interfaces.isVisible(INTERFACE_INDEX, 0);
    }

    public static boolean close() {
        if (!isOpen()) {
            return true;
        }
        return CLOSE.mapToBoolean(x -> x.interact("Close")) && Time.sleepUntil(() -> !isOpen(), 1200);
    }

    //TODO this is BROKE
    /*public static boolean select(Predicate<String> predicate) {
        InterfaceComponent[] buttons = Interfaces.getComponents(INTERFACE_INDEX, x -> x.containsAction("Select"));
        for (InterfaceComponent btn : buttons) {
            for (InterfaceComponent cmp : Interfaces.getComponents(INTERFACE_INDEX, InterfaceComponent::isVisible)) {
                String txt = cmp.getText();
                if (!txt.isEmpty() && Character.isDigit(txt.charAt(0)) && predicate.test(cmp.getText())) {
                    if (btn.interact("Select")) {
                        return Time.sleepUntil(() -> !isOpen(), 1200);
                    }
                }
            }
        }
        return false;
    }*/

    public static boolean select(Predicate<String> predicate) {
        InterfaceComponent[] selectors = Interfaces.getComponents(INTERFACE_INDEX, x -> x.getTooltip() != null && x.getTooltip().equals("Select"));
        InterfaceComponent getName = Interfaces.getFirst(INTERFACE_INDEX, a -> predicate.test(a.getText()));
        if (getName != null && selectors.length > 0) {
            int index = Character.getNumericValue(getName.getText().charAt(0));
            ActionProcessor.submit(Action.valueOf(ActionOpcodes.OP_BUTTON, 0, -1, selectors[index - 1].getUid()));
            return true;
        }
        return false;
    }
}

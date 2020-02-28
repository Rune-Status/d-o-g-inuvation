package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.api.action.tree.Action;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.event.listener.MenuActionListener;

public final class MenuActionEvent extends Event<String> {

    private final int opcode;
    private final long primary;
    private final int secondary, tertiary;
    private final String target;

    private final Action resolved;

    private boolean consumed = false;

    public MenuActionEvent(int opcode, long primary, int secondary, int tertiary,
                           String text, String targetText, Action resolved) {
        super(text, "Static");
        this.opcode = opcode;
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
        this.target = targetText;
        this.resolved = resolved;
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof MenuActionListener) {
            ((MenuActionListener) listener).notify(this);
        }
    }

    public int getOpcode() {
        return opcode;
    }

    public long getPrimary() {
        return primary;
    }

    public int getSecondary() {
        return secondary;
    }

    public int getTertiary() {
        return tertiary;
    }

    public String getTarget() {
        return target;
    }

    public Action toAction() {
        return resolved;
    }

    @Override
    public String toString() {
        if (resolved != null) {
            return resolved.toString();
        }
        return String.format("UNKNOWN action: %d[%d,%d,%d]%n", opcode, primary, secondary, tertiary);
    }

    public void consume() {
        consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }
}

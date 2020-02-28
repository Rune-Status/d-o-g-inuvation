package org.rspeer.game.event.types;

import org.rspeer.event.Event;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.game.event.listener.RuneScriptListener;
import org.rspeer.game.providers.RSScriptContext;

import java.util.Arrays;

public final class RuneScriptEvent extends Event<RSScriptContext> {

    public RuneScriptEvent(RSScriptContext source) {
        super(source, "Static");
    }

    public int getScript() {
        return (Integer) getSource().getArgs()[0];
    }

    public Object[] getArgs() {
        Object[] objects = getSource().getArgs();
        Object[] ret = new Object[objects.length - 1];
        if (objects.length - 1 >= 0) {
            System.arraycopy(objects, 1, ret, 0, objects.length - 1);
        }
        return ret;
    }

    public String getMenuOption() {
        return getSource().getMenuOption();
    }

    @Override
    public void forward(EventListener listener) {
        if (listener instanceof RuneScriptListener) {
            ((RuneScriptListener) listener).notify(this);
        }
    }

    @Override
    public String toString() {
        return "RuneScriptEvent: { script: " +  getScript() + " args: " + Arrays.toString(getArgs()) + "}";
    }
}

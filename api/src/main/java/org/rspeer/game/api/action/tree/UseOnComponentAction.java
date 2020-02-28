package org.rspeer.game.api.action.tree;

import org.rspeer.game.adapter.component.InterfaceComponent;

import static org.rspeer.game.api.action.ActionOpcodes.USE_ON_BUTTON;
import static org.rspeer.game.api.action.ActionOpcodes.USE_ON_COMPONENT;

public final class UseOnComponentAction extends ComponentAction {

    public UseOnComponentAction(boolean button, int subcomponentIndex, int componentUid) {
        super(button ? USE_ON_BUTTON : USE_ON_COMPONENT, 0, subcomponentIndex, componentUid);
    }

    public UseOnComponentAction(boolean button, InterfaceComponent component) {
        this(button, component.getComponentIndex(), component.getUid());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[group=")
                .append(getGroupIndex())
                .append(",component=")
                .append(getComponentIndex());

        if (getSubcomponentIndex() != -1) {
            builder.append(",subcomponent=")
                    .append(getSubcomponentIndex());
        }

        return builder.append("]").toString();
    }
}

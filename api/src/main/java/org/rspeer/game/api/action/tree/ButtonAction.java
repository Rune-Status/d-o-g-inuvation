package org.rspeer.game.api.action.tree;

import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.action.ActionOpcodes;

public final class ButtonAction extends ComponentAction {

    public ButtonAction(int subcomponentIndex, int componentUid) {
        super(ActionOpcodes.OP_BUTTON, 0, subcomponentIndex, componentUid);
    }

    public ButtonAction(InterfaceComponent component) {
        this(component.getComponentIndex(), component.getUid());
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

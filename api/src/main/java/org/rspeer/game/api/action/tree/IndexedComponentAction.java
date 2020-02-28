package org.rspeer.game.api.action.tree;

import org.rspeer.game.adapter.component.InterfaceComponent;

import static org.rspeer.game.api.action.ActionOpcodes.OP_COMPONENT1;
import static org.rspeer.game.api.action.ActionOpcodes.OP_COMPONENT2;

public final class IndexedComponentAction extends ComponentAction {

    public IndexedComponentAction(int actionIndex, int subcomponentIndex, int componentUid) {
        super(actionIndex > 5 ? OP_COMPONENT2 : OP_COMPONENT1, actionIndex, subcomponentIndex, componentUid);
    }

    public IndexedComponentAction(int actionIndex, InterfaceComponent component) {
        this(actionIndex, component.getComponentIndex(), component.getUid());
    }

    public int getActionIndex() {
        return (int) primary;
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

        return builder.append(",actionIndex=")
                .append(getActionIndex())
                .append("]").toString();
    }
}

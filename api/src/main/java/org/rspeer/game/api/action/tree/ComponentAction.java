package org.rspeer.game.api.action.tree;

import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.component.Interfaces;

public abstract class ComponentAction extends Action {

    protected ComponentAction(int opcode, long primary, int secondary, int tertiary) {
        super(opcode, primary, secondary, tertiary);
    }

    public int getUid() {
        return tertiary;
    }

    public int getSubcomponentIndex() {
        return secondary;
    }

    public boolean isGrandchild() {
        return secondary != -1;
    }

    public int getGroupIndex() {
        return getUid() >>> 16;
    }

    public int getComponentIndex() {
        return getUid() & 0xffff;
    }

    public InterfaceComponent getSource() {
        if (secondary == -1) {
            return Interfaces.getComponent(getGroupIndex(), getComponentIndex());
        }
        return Interfaces.getComponent(getGroupIndex(), getComponentIndex(), getSubcomponentIndex());
    }
}

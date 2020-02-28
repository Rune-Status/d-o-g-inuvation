package org.rspeer.game.api.action.tree;

import org.rspeer.game.adapter.scene.Mobile;

public abstract class MobileAction<K extends Mobile> extends Action {

    protected MobileAction(int opcode, long index) {
        super(opcode, index, 0, 0);
    }

    public int getEntityIndex() {
        return (int) primary;
    }

    public abstract K getSource();
}

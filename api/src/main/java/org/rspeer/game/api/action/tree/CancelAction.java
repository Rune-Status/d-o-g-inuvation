package org.rspeer.game.api.action.tree;

import org.rspeer.game.api.action.ActionOpcodes;

public final class CancelAction extends Action {

    public CancelAction() {
        super(ActionOpcodes.CANCEL, 0, 0, 0);
    }
}

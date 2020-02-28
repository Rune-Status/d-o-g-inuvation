package org.rspeer.game.api.action.tree;

import org.rspeer.game.api.action.ActionOpcodes;
import org.rspeer.game.api.position.Position;
import org.rspeer.game.api.position.ScenePosition;

public final class WalkAction extends Action {

    private final Position position;

    public WalkAction(ScenePosition position) {
        super(ActionOpcodes.WALK, 0, position.getX(), position.getY());
        this.position = position.getPosition();
    }

    public WalkAction(int sceneX, int sceneY) {
        this(Position.regional(sceneX, sceneY));
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("%s[x=%d,y=%d]", super.toString(), position.getX(), position.getY());
    }
}

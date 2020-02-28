package org.rspeer.game.api.action.tree;

import org.rspeer.api.commons.Functions;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.api.position.Position;
import org.rspeer.game.api.scene.SceneObjects;

public final class ObjectAction extends Action {

    private final Position position;

    public ObjectAction(int opcode, long uid, int sceneX, int sceneY) {
        super(opcode, uid, sceneX, sceneY);
        position = Position.regional(sceneX, sceneY).getPosition();
    }

    public long getUid() {
        return primary;
    }

    public int getId() {
        //long l = (getUid() >> 32) | 1; //the | 1 is situational based on another hook,
        //so i took the lazy way out
        return Functions.mapOrM1(this::getSource, SceneObject::getId);
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[x=")
                .append(position.getX())
                .append(",y=")
                .append(position.getY());
        SceneObject obj = getSource();
        if (obj != null) {
            builder.append(",id=")
                    .append(obj.getId())
                    .append(",type=")
                    .append(obj.getType())
                    .append(",orientation=")
                    .append(obj.getOrientation());
        }
        return builder.append("]").toString();
    }

    public SceneObject getSource() {
        return SceneObjects.getFirstAt(position, x -> x.getUid() == primary);
    }
}

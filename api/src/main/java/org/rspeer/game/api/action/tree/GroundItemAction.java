package org.rspeer.game.api.action.tree;

import org.rspeer.game.adapter.cache.ItemDefinition;
import org.rspeer.game.adapter.scene.GroundItem;
import org.rspeer.game.api.Definitions;

public final class GroundItemAction extends Action {

    public GroundItemAction(int opcode, int itemId, int sceneX, int sceneY) {
        super(opcode, itemId, sceneX, sceneY);
    }

    public GroundItemAction(int opcode, GroundItem item) {
        this(opcode, item.getId(), item.getScenePosition().getX(), item.getScenePosition().getY());
    }

    private ItemDefinition getDefinition() {
        return Definitions.getItem((int) primary);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[id=").append(primary)
                .append(",x=").append(secondary)
                .append(",y=").append(tertiary);
        ItemDefinition definition = getDefinition();
        if (definition != null) {
            builder.append(",name=").append(definition.getName());
        }
        builder.append("]");
        return builder.toString();
    }
}

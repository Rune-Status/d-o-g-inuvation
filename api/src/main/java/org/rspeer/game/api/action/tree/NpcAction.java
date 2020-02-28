package org.rspeer.game.api.action.tree;

import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.api.Game;
import org.rspeer.game.providers.RSNodeTable;
import org.rspeer.game.providers.RSNpc;
import org.rspeer.game.providers.RSObjectNode;

public final class NpcAction extends MobileAction<Npc> {

    public NpcAction(int opcode, long index) {
        super(opcode, index);
    }

    public NpcAction(int opcode, Npc npc) {
        this(opcode, npc.getIndex());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[index=")
                .append(getEntityIndex());
        Npc npc = getSource();
        if (npc != null) {
            builder.append(",name=")
                    .append(npc.getName())
                    .append(",id=")
                    .append(npc.getId())
                    .append(",x=")
                    .append(npc.getX())
                    .append(",y=")
                    .append(npc.getY());
        }
        return builder.append("]").toString();
    }

    public Npc getSource() {
        RSNodeTable<RSObjectNode> table = Game.getClient().getNpcObjectNodeTable();
        if (table != null) {
            RSObjectNode node = table.getSynthetic(getEntityIndex());
            if (node != null) {
                Object referent = node.getReferent();
                if (referent instanceof RSNpc) {
                    return ((RSNpc) referent).getAdapter();
                }
            }
        }
        return null;
    }
}

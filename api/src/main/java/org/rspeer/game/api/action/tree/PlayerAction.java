package org.rspeer.game.api.action.tree;

import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.api.Game;
import org.rspeer.game.providers.RSPlayer;

public final class PlayerAction extends MobileAction<Player> {

    public PlayerAction(int opcode, long index) {
        super(opcode, index);
    }

    public PlayerAction(int opcode, Player player) {
        this(opcode, player.getIndex());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[index=")
                .append(getEntityIndex());
        Player player = getSource();
        if (player != null) {
            builder.append(",name=")
                    .append(player.getName())
                    .append(",x=")
                    .append(player.getX())
                    .append("y=")
                    .append(player.getY());
        }
        return builder.append("]").toString();
    }

    public Player getSource() {
        RSPlayer[] players = Game.getClient().getPlayers();
        int idx = getEntityIndex();
        return idx >= 0 && idx < players.length && players[idx] != null ? players[idx].getAdapter() : null;
    }
}

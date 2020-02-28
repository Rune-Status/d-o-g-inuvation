package org.rspeer.game.event.callback;

import org.rspeer.game.adapter.scene.Npc;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.action.ActionOpcodes;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.tree.Action;
import org.rspeer.game.api.component.Item;
import org.rspeer.game.api.component.tab.Backpack;
import org.rspeer.game.api.query.results.ItemQueryResults;
import org.rspeer.game.api.scene.Npcs;
import org.rspeer.game.api.scene.Players;

import java.util.Arrays;

public final class DefaultCommandProcessor implements CommandProcessor {

    @Override
    public boolean accept(String cmd) {
        if (cmd.equals("logout")) {
            Game.getClient().getActiveConnection().close();
        } else if (cmd.equals("help")) {
            Game.getClient().printToConsole("backpack - displays backpack information \n" +
                    "position - displays local players position \n" +
                    "npcs - displays nearest npcs info \n" +
                    "logout - logs out to login screen");
        } else if (cmd.equals("npcs")) {
            debugNpcs();
        } else if (cmd.equals("backpack")) {
            debugBackpack();
        } else if (cmd.equals("position")) {
            Player player = Players.getLocal();
            Game.getClient().printToConsole(player == null ? "null" : player.getPosition().toString());
        } else if (cmd.startsWith("cmd ")) {
            fireAction(cmd);
            return true;
        }
        return false;
    }

    private void debugNpcs() {
        for (Npc npc : Npcs.getLoaded()) {
            StringBuilder output = new StringBuilder()
                    .append("Name: ").append(npc.getName()).append(",")
                    .append("Id: ").append(npc.getId()).append(",")
                    .append("Actions: ").append(Arrays.toString(npc.getActions())).append(",")
                    .append("Animation: ").append(npc.getAnimation());
            Game.getClient().printToConsole(output.toString());
        }
    }

    private void debugBackpack() {
        if (!Backpack.isEmpty()) {
            ItemQueryResults items = Backpack.getItems();
            for (Item item : items) {
                StringBuilder output = new StringBuilder()
                        .append("Name: ").append(item.getName()).append(",")
                        .append("Id").append(item.getId()).append(",")
                        .append("Quantity: ").append(item.getStackSize()).append(",")
                        .append("Actions: ").append(Arrays.toString(item.getActions()));
                Game.getClient().printToConsole(output.toString());
            }
        } else {
            Game.getClient().printToConsole("Backpack is empty");
        }
    }

    private void fireAction(String cmd) {
        String command = cmd.replace("cmd", "").trim();
        String[] actions = command.split("\\+");
        if (actions.length == 4) {
            try {
                int arg0 = ActionOpcodes.class.getField(actions[0].toUpperCase()).getInt(null);
                long arg1 = Long.parseLong(actions[1]);
                int arg2 = Integer.parseInt(actions[2]);
                int arg3 = Integer.parseInt(actions[3]);

                ActionProcessor.submit(Action.valueOf(arg0, arg1, arg2, arg3));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        } else {
            Game.getClient().printToConsole("command wrong length!");
        }
    }
}

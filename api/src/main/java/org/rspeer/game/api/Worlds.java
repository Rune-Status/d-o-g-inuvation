package org.rspeer.game.api;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.world.World;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.query.WorldQueryBuilder;
import org.rspeer.game.providers.RSWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class Worlds {

    //TODO figure out what method to invoke to switch worlds etc

    private static final int WORLD_SELECT_GROUP = InterfaceComposite.WORLD_HOP.getGroup();

    private static final InterfaceAddress WORLD_OPEN_ADDRESS = new InterfaceAddress(() -> Interfaces.getFirst(1465, a -> a.containsAction("Open World Select")));
    private static final InterfaceAddress WORLD_ADDRESS = new InterfaceAddress(() -> Interfaces.getFirst(550, e -> e.getText().contains("Friends List") && !e.getText().contains("Please wait")));

    private Worlds() {
        throw new IllegalAccessError();
    }

    /**
     * @deprecated
     */
    //replace with queryresults
    public static World[] getLoaded(Predicate<World> predicate) {
        List<World> worlds = new ArrayList<>();
        RSWorld[] rawWorlds = Game.getClient().getWorlds();
        for (int i = 0; i < rawWorlds.length; i++) {//static cap to prevent detecting worlds we arnt supposed to see lmao
            RSWorld world = rawWorlds[i];
            if (world != null) {
                World w = world.getAdapter();
                if (w.getWorld() > 140)
                    break;
                if (predicate.test(w)) {
                    w.setWorld(i + 1);
                    worlds.add(world.getAdapter());
                }
            }
        }
        return worlds.toArray(new World[0]);
    }

    public static WorldQueryBuilder newQuery() {
        return new WorldQueryBuilder();
    }

    public static World get(int world) {
        RSWorld rawWorld = Game.getClient().getWorlds()[world - 1];
        if (rawWorld != null) {
            World adapter = rawWorld.getAdapter();
            adapter.setWorld(world);
            return adapter;
        }
        return null;
    }

    public static World getCurrent() {
        InterfaceComponent component = WORLD_ADDRESS.resolve();
        if (component != null) {
            String text = component.getText();
            if (text != null && text.contains("<br>")) {
                return get(Integer.parseInt(text.split("RuneScape ")[1]));
            }
        }
        return null;
    }

    public static boolean isWorldHopOpen() {
        return Interfaces.isVisible(WORLD_SELECT_GROUP, 1);
    }

    public static boolean openWorldHop() {
        InterfaceComponent component = WORLD_OPEN_ADDRESS.resolve();
        return component != null && component.interact("Open World Select")
                && Time.sleepUntil(Worlds::isWorldHopOpen, 1800);
    }

    public static boolean hopTo(int world) {
        World w = get(world);
        if (w != null) {
            w.hopTo();
            return true;
        }
        return false;
    }

    public static boolean classicHopTo(int world) {
        if (!isWorldHopOpen() && !openWorldHop()) {
            return false;
        }
        InterfaceComponent component = Interfaces.getComponent(WORLD_SELECT_GROUP, 8, world);
        return component != null && component.interact("Quick-hop") && Time.sleepUntil(() -> {
            World current = getCurrent();
            return current != null && current.getWorld() == world;
        }, 10000);
    }
}

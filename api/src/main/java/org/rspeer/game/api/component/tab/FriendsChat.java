package org.rspeer.game.api.component.tab;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.component.EnterInput;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.component.TeleportOptions;

public final class FriendsChat {

    private static final InterfaceAddress FAVORITES_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(1427, a -> a.containsAction("Open"))
    );

    private static final InterfaceAddress JOIN_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(1427, a -> a.containsAction("Join chat"))
    );

    private static final InterfaceAddress LEAVE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(1427, a -> a.containsAction("Leave chat"))
    );

    private static final InterfaceAddress OWNER_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(1427, a -> a.getText().contains("Owner:"))
    );

    private static final InterfaceAddress VIEW_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(1477, a -> a.getText().equals("Friends Chat List"))
    );

    private static final InterfaceAddress EXPAND_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(1432, a -> a.getName().equals("Friends Chat List"))
    );

    private static boolean openFavourites() {
        return FAVORITES_ADDRESS.mapToBoolean(x -> x.interact("Open")) && Time.sleepUntil(TeleportOptions::isOpen, 1200);
    }

    public static boolean joinFavourite(String owner) {
        if (TeleportOptions.isOpen()) {
            TeleportOptions.select(a -> a.contains(owner));
            return Time.sleepUntil(FriendsChat::isInChannel, 3600);
        }
        return openFavourites();
    }

    public static boolean addFavourite(String owner) {
        if (isOwner(owner)) {
            if (TeleportOptions.isOpen()) {
                TeleportOptions.select(a -> a.contains("Empty"));
                return Time.sleepUntil(() -> !TeleportOptions.isOpen(), 1200);
            } else {
                openFavourites();
            }
        } else if (!isInChannel()) {
            join(owner);
        } else {
            leave();
        }
        return false;
    }

    public static boolean join(String owner) {
        if (EnterInput.isOpen()) {
            return EnterInput.initiate(owner) && Time.sleepUntil(FriendsChat::isInChannel, 1200);
        }

        if (JOIN_ADDRESS.mapToBoolean(x -> x.interact("Join chat"))) {
            Time.sleepUntil(EnterInput::isOpen, 1200);
        }
        return false;
    }

    public static boolean leave() {
        return LEAVE_ADDRESS.mapToBoolean(x -> x.interact("Leave chat")) && Time.sleepUntil(() -> !isInChannel(), 1200);
    }

    public static boolean isInChannel() {
        return OWNER_ADDRESS.resolve() != null;
    }

    public static boolean isOwner(String owner) {
        InterfaceComponent cmp = OWNER_ADDRESS.resolve();
        return cmp != null && cmp.getName().contains(owner);
    }

    public static boolean isOpen() {
        return VIEW_ADDRESS.resolve() != null;
    }

    public static boolean open() {
        return isOpen() || EXPAND_ADDRESS.mapToBoolean(x -> x.interact(y -> true));
    }
}

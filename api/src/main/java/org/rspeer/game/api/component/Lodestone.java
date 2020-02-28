package org.rspeer.game.api.component;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.Player;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.tree.IndexedComponentAction;
import org.rspeer.game.api.position.Position;
import org.rspeer.game.api.scene.Players;

public enum Lodestone {

    BANDIT_CAMP(Position.global(3214, 2954, 0), -12),
    LUNAR_ISLE(Position.global(2085, 3914, 0), -1),
    AL_KHARID(Position.global(3297, 3184, 0), 0),
    ARDOUGNE(Position.global(2634, 3348, 0), 1),
    BURTHORPE(Position.global(2899, 3544, 0), 2),
    CATHERBY(Position.global(2831, 3451, 0), 3),
    DRAYNOR(Position.global(3105, 3298, 0), 4),
    EDGEVILLE(Position.global(3067, 3505, 0), 5),
    FALADOR(Position.global(2967, 3403, 0), 6),
    LUMBRIDGE(Position.global(3233, 3221, 0), 7),
    PORT_SARIM(Position.global(3011, 3215, 0), 8),
    SEERS_VILLAGE(Position.global(2689, 3482, 0), 9),
    TAVERLEY(Position.global(2878, 3441, 0), 10),
    VARROCK(Position.global(3214, 3376, 0), 11),
    MENAPHOS(Position.global(3216, 2718, 0), -1),
    ANACHRONIA(Position.global(5431, 2338, 0), -1),
    YANILLE(Position.global(2529, 3094, 0), 12),
    CANIFIS(Position.global(3518, 3517, 0), 15),
    EAGLES_PEEK(Position.global(2366, 3479, 0), 16),
    FREMENIK_PROVINCE(Position.global(2711, 3678, 0), 17),
    KARAMJA(Position.global(2761, 3148, 0), 18),
    OOGLOG(Position.global(2533, 2871, 0), 19),
    TIRANNWN(Position.global(2254, 3150, 0), 20),
    WILDERNESS_VOLCANO(Position.global(3142, 3636, 0), 21),
    ASHDALE(Position.global(2460, 2686, 0), -1),
    PRIFDDINAS(Position.global(2206, 3362, 1), 24);

    private static final int LODESTONE_GROUP_INDEX = 1092;
    private static final int MAP_BUTTON_GROUP_INDEX = 1465;

    private static final InterfaceAddress LODESTONE_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(LODESTONE_GROUP_INDEX, a -> true)
    );

    private static final InterfaceAddress MAP_ICON_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(MAP_BUTTON_GROUP_INDEX, a -> a.containsAction("Lodestone network"))
    );

    private final InterfaceAddress teleportAddress = new InterfaceAddress(
            () -> Interfaces.getFirst(LODESTONE_GROUP_INDEX, a -> a.getIndex() == 9 + ordinal()) // Can be changed to material id if needed
    );

    private final InterfaceAddress mruTeleportAddress = new InterfaceAddress(
            () -> Interfaces.getFirst(MAP_BUTTON_GROUP_INDEX, a -> a.containsAction(
                    x -> x.toLowerCase().equals(name().toLowerCase().replace("_", " "))))
    );

    private final Position position;
    private final int shift;

    Lodestone(Position position, int shift) {
        this.position = position;
        this.shift = shift;
    }

    public static boolean isOpen() {
        InterfaceComponent component = LODESTONE_ADDRESS.resolve();
        return component != null && component.isVisible();
    }

    public boolean teleport(boolean quickTeleport) {
        Player local = Players.getLocal();
        if (!isUnlocked() || position.distance(local) < 10) {
            return false;
        }

        if (this == WILDERNESS_VOLCANO && Dialog.isOpen()) {
            new Dialog(false, 1).process();
            return false;
        }

        InterfaceComponent previousTeleport = mruTeleportAddress.resolve();
        if (previousTeleport != null && previousTeleport.interact(x -> x.toLowerCase()
                .equals(name().replace("_", " ").toLowerCase() + " lodestone"))) {
            return Time.sleepUntil(() -> position.distance(local) < 10, () -> local.getAnimation() != -1, 3000);
        }

        if (!isOpen()) {
            InterfaceComponent lodestoneNetwork = MAP_ICON_ADDRESS.resolve();
            if (lodestoneNetwork == null)
                return false;

            int count = 0;
            for (String a : lodestoneNetwork.getActions()) {
                if (a.equals("Lodestone network")) {
                    count++;
                }
            }

            if (count == 1) {
                lodestoneNetwork.interact("Lodestone network");
            } else {
                ActionProcessor.submit(new IndexedComponentAction(1, lodestoneNetwork));
            }
            return false;
        }

        InterfaceComponent component = teleportAddress.resolve();
        if (component != null && component.interact(quickTeleport ? "Quick Teleport" : "Teleport")) {
            return Time.sleepUntil(() -> position.distance(local) < 10, () -> local.getAnimation() != -1, 3000);
        }
        return false;
    }

    public boolean teleport() {
        return teleport(false);
    }

    public boolean isUnlocked() {
        switch (this) {
            case BANDIT_CAMP:
                return (Varps.getValue(2151) & 0x7fff) == 15;
            case LUNAR_ISLE:
                return (Varps.getValue(2253) & 0xfffff) == 190;
            case ASHDALE:
                return (Varps.getValue(4390) & 0x7f) == 100;
            case MENAPHOS:
                return (Varps.getValue(7040) & 1) == 1;
        }
        return ((Varps.getValue(3) >>> shift) & 1) == 1;
    }

    public Position getPosition() {
        return position;
    }
}

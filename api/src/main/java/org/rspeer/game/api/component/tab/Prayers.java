package org.rspeer.game.api.component.tab;

import org.rspeer.game.api.VarpComposite;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;

public final class Prayers {

    public static final int GROUP_INDEX = InterfaceComposite.PRAYER.getGroup();

    private static final int VARPBIT_QUICK = 5941;

    private static final InterfaceAddress QUICK_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(InterfaceComposite.MAIN_ACTIONBAR.getGroup(),
                    a -> a.containsAction(n -> n.startsWith("Turn"))));

    private Prayers() {
        throw new IllegalAccessError();
    }

    public static int getPoints() {
        return (Varps.getValue(VarpComposite.PRAYER_POINTS) & 0x7fff) / 10;
    }

    public static int getPointPercent() {
        return getPoints() * 100 / getMaximumPoints();
    }

    public static int getMaximumPoints() {
        return Skills.getLevel(Skill.PRAYER) * 10;
    }

    public static boolean isQuickPraying() {
        return Varps.getBitValue(VARPBIT_QUICK) > 0;
    }

    public static boolean toggleQuickPrayers() {
        return QUICK_ADDRESS.mapToBoolean(x -> x.interact(y -> y.startsWith("Turn")));
    }

    public static boolean activate(Prayer prayer) {
        return prayer.activate();
    }

    public static boolean deactivate(Prayer prayer) {
        return prayer.deactivate();
    }

    public static Prayer.Book getBook() {
        int value = Varps.getValue(3277);
        switch (value) {
            case 20:
                return Prayer.Book.MODERN;

            case 21:
                return Prayer.Book.ANCIENT;

            default:
                throw new IllegalStateException("Unknown prayer book: " + value);
        }
    }
}

package org.rspeer.game.api;

import org.rspeer.api.commons.Functions;
import org.rspeer.game.providers.RSPlayerFacade;
import org.rspeer.game.providers.RSVarpBit;
import org.rspeer.game.providers.RSVarps;

public final class Varps {

    public static final int[] BIT_MASKS = new int[32];

    static {
        int delta = 2;
        for (int mask = 0; mask < 32; ++mask) {
            BIT_MASKS[mask] = delta - 1;
            delta += delta;
        }
    }

    private Varps() {
        throw new IllegalAccessError();
    }

    private static RSVarps getProvider() {
        return Functions.mapOrNull(Game::getPlayerFacade, RSPlayerFacade::getVarps);
    }

    public static int getValue(int index) {
        return Functions.mapOrDefault(Varps::getProvider, e -> e.getValueAt(index), -1);
    }

    public static int getValue(VarpComposite composite) {
        return getValue(composite.getIndex());
    }

    public static RSVarpBit getBit(int index) {
        RSPlayerFacade facade = Game.getPlayerFacade();
        if (facade != null) {
            try {
                return facade.getVarpBit(index);
            } catch (Exception e) {

            }
        }
        return null;
    }

    public static int getBitValue(int index) {
        return Functions.mapOrDefault(() -> getBit(index), RSVarpBit::getValue, -1);
    }

    public static int getBitValue(VarpbitComposite composite) {
        return getBitValue(composite.getIndex());
    }

    public static int[] getValues() {
        return Functions.mapOrDefault(Varps::getProvider, RSVarps::getValues, new int[0]);
    }
}

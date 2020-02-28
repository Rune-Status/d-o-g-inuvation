package org.rspeer.game.api.combat;

import org.rspeer.game.api.Varps;

public enum CombatStyle {

    MELEE,
    RANGED,
    MAGIC;

    private static final int STYLE_VARP = 717;

    public static CombatStyle getCurrent() {
        int value = Varps.getValue(STYLE_VARP);
        if (value == 1) {
            return MAGIC;
        } else if (value == 8 || value == 9 || value == 10) {
            return RANGED;
        } else {
            return MELEE;
        }
    }
}

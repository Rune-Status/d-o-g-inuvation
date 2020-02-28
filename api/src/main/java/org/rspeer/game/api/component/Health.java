package org.rspeer.game.api.component;

import org.rspeer.game.api.Varps;
import org.rspeer.game.api.component.tab.Skill;
import org.rspeer.game.api.component.tab.Skills;

public final class Health {

    private static final int VALUE_BIT = 1668;
    private static final int POISON_BIT = 2104;

    private Health() {
        throw new IllegalAccessError();
    }

    public static int getCurrent() {
        return Varps.getBitValue(VALUE_BIT);
    }

    public static int getCurrentPercent() {
        double max = Skills.getLevel(Skill.CONSTITUTION) * 100D;
        return (int) ((double) getCurrent() * 100D / max);
    }

    public static boolean isPoisoned() {
        return Varps.getBitValue(POISON_BIT) > 0;
    }
}

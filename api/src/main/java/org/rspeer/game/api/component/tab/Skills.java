package org.rspeer.game.api.component.tab;

import org.rspeer.api.commons.Functions;
import org.rspeer.game.api.Game;
import org.rspeer.game.providers.RSExpTable;
import org.rspeer.game.providers.RSSkill;
import org.rspeer.game.providers.RSSkillLevel;

public final class Skills {

    public static final int MAX_EXPERIENCE = 200_000_000;

    private Skills() {
        throw new IllegalAccessError();
    }

    private static RSSkill getProvider(Skill skill) {
        return Game.getPlayerFacade().getSkill(skill.getIndex());
    }

    public static int getLevel(Skill skill) {
        return Functions.mapOrM1(() -> getProvider(skill), RSSkill::getLevel);
    }

    public static int getCurrentLevel(Skill skill) {
        return Functions.mapOrM1(() -> getProvider(skill), RSSkill::getCurrentLevel);
    }

    public static int getExperience(Skill skill) {
        return Functions.mapOrM1(() -> getProvider(skill), RSSkill::getExperience);
    }

    private static RSExpTable getExpTable(Skill skill) {
        RSSkillLevel level = Functions.mapOrNull(() -> getProvider(skill), RSSkill::getLevelData);
        return level != null ? level.getExpTable() : null;
    }

    public static int getExperienceAt(Skill skill, int level) {
        int[] table = Functions.mapOrDefault(() -> getExpTable(skill), RSExpTable::getTable, new int[120]);
        level -= 2;

        if (level <= 1) {
            return 0;
        }

        if (skill == Skill.INVENTION) {
            level++; //not sure why their xp table arrays are structured inconsistently
        }

        return level < table.length ? table[level] : MAX_EXPERIENCE;
    }

    public static int getExperienceAt(int level) {
        //doesnt matter which skill it defaults to as long as its not an elite skill
        return getExperienceAt(Skill.AGILITY, level);
    }

    public static int getExperienceToNextLevel(Skill skill) {
        int nextLvl = getVirtualLevel(skill) + 1;
        if (nextLvl > 120) {
            return MAX_EXPERIENCE - getExperience(skill);
        }
        return getExperienceAt(nextLvl) - getExperience(skill);
    }

    public static int getVirtualLevel(Skill skill) {
        int xp = getExperience(skill);
        int[] table = Functions.mapOrDefault(() -> getExpTable(skill), RSExpTable::getTable, new int[120]);
        for (int i = table.length - 1; i > 0; i--) {
            int xpat = table[i];
            if (xp >= xpat) {
                int level = i + 2;
                return level > 120 ? 120 : level;
            }
        }
        return -1;
    }

    public static int getLevelAt(Skill skill, int xp) {
        int[] table = Functions.mapOrDefault(() -> getExpTable(skill), RSExpTable::getTable, new int[120]);
        for (int i = table.length - 1; i > 0; i--) {
            if (i <= 120) {
                int xpat = table[i];
                if (xp >= xpat) {
                    return i + 2;
                }
            }
        }
        return -1;
    }

    public static int getLevelAt(int xp) {
        //doesnt matter which skill it defaults to as long as its not an elite skill
        return getLevelAt(Skill.AGILITY, xp);
    }

    public static int getTotalExperience() {
        int xp = 0;
        for (Skill skill : Skill.values()) {
            xp += getExperience(skill);
        }
        return xp;
    }
}

package org.rspeer.game.api.component.tab;

public enum Skill {

    ATTACK,
    DEFENCE,
    STRENGTH,
    CONSTITUTION,
    RANGED,
    PRAYER,
    MAGIC,
    COOKING,
    WOODCUTTING,
    FLETCHING,
    FISHING,
    FIREMAKING,
    CRAFTING,
    SMITHING,
    MINING,
    HERBLORE,
    AGILITY,
    THIEVING,
    SLAYER,
    FARMING,
    RUNECRAFTING,
    HUNTER,
    CONSTRUCTION,
    SUMMONING,
    DUNGEONEERING,
    DIVINATION,
    INVENTION;

    public int getIndex() {
        return ordinal();
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
    }
}

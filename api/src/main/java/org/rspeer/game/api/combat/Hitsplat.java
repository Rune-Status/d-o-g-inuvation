package org.rspeer.game.api.combat;

import org.rspeer.game.api.Game;

public final class Hitsplat {

    private final int damage;
    private final int id;
    private final int cycle;

    public Hitsplat(int damage, int id, int cycle) {
        this.damage = damage;
        this.id = id;
        this.cycle = cycle;
    }

    public int getDamage() {
        return damage;
    }

    public int getId() {
        return id;
    }

    public int getCycle() {
        return cycle;
    }

    public Type getType() {
        for (Type type : Type.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }

    public boolean isActive() {
        return Game.getClient().getEngineCycle() < cycle;
    }

    public enum Type {

        MELEE_AUTO_ATTACK(132),
        MELEE_CRITICAL_HIT(134),
        MELEE_ABILITY(133),

        MAGIC_AUTO_ATTACK(138),
        MAGIC_CRITICAL_HIT(140),
        MAGIC_ABILITY(139),

        RANGED_AUTO_ATTACK(135),
        RANGED_CRITICAL_HIT(137),
        RANGED_ABILITY(136),

        HEAL(143),
        TYPELESS(144),
        MISS(141),
        POISON(142),
        BLIGHT(232),

        BIG_GAME_HUNTER_BLUE(233),
        BIG_GAME_HUNTER_YELLOW(235),
        BIG_GAME_HUNTER_RED(250);

        private final int id;

        Type(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}

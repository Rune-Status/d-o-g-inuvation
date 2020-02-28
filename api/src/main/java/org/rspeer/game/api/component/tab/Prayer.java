package org.rspeer.game.api.component.tab;

import org.rspeer.api.commons.Time;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.Interfaces;

public interface Prayer {

    int getLevelRequirement();

    boolean isActivated();

    InterfaceAddress getAddress();

    Book getBook();

    default boolean activate() {
        if (isActivated() || Skills.getLevel(Skill.PRAYER) < getLevelRequirement()) {
            return false;
        }
        InterfaceComponent component = getAddress().resolve(x -> x.interact(y -> y.contains("Activate")));
        return component != null && Time.sleepUntil(this::isActivated, 2000);
    }

    default boolean deactivate() {
        if (!isActivated()) {
            return false;
        }
        InterfaceComponent component = getAddress().resolve(x -> x.interact(y -> y.contains("Deactivate")));
        return component != null && Time.sleepWhile(this::isActivated, 2000);
    }

    enum Book {
        MODERN, ANCIENT
    }

    enum Modern implements Prayer { // Needs some reordering at some point

        STEEL_SKIN(28, 25956, 16739),
        ULTIMATE_STRENGTH(31, 25957, 16740),
        INCREDIBLE_REFLEXES(34, 25958, 16741),
        OVERPOWERING_FORCE(44, 25981, 16753),
        EAGLE_EYE(44, 25969, 16751),
        OVERCHARGE(45, 25978, 16754),
        MYSTIC_MIGHT(45, 25970, 16752),
        RAPID_RESTORE(19, 25953, 16742),
        RAPID_HEAL(22, 25954, 16743),
        PROTECT_ITEM(25, 25955, 16744),
        PROTECT_FROM_SUMMONING(35, 25971, 16755),
        PROTECT_FROM_MAGIC(37, 25959, 16745),
        PROTECT_FROM_MISSILES(40, 25960, 16746),
        PROTECT_FROM_MELEE(43, 25961, 16747),
        RETRIBUTION(46, 25963, 16748),
        REDEMPTION(49, 25962, 16749),
        SMITE(52, 25964, 16750),
        CHIVALRY(60, 25972, 16756),
        PIETY(70, 25973, 16757),
        RIGOUR(70, 25982, 16760),
        AUGURY(70, 25974, 16759),
        CLARITY_OF_THOUGHT(7, 25949, -1),
        SHARP_EYE(8, 25965, -1),
        THICK_SKIN(1, 25947, -1),
        ROCK_SKIN(10, 25950, -1),
        MYSTIC_WILL(9, 25966, -1),
        SUPERHUMAN_STRENGTH(13, 25951, -1),
        RAPID_RENEWAL(65, -1, -1),
        IMPROVED_REFLEXES(16, 25952, -1),
        MYSTIC_LORE(27, 25968, -1),
        BURST_OF_STRENGTH(4, 25948, -1),
        SUPER_CHARGE(27, 25977, -1),
        UNRELENTING_FORCE(26, 25980, -1),
        CHARGE(9, 25976, -1),
        UNSTOPPABLE_FORCE(8, 25979, -1),
        HAWK_EYE(26, 25967, -1);

        private final int levelRequirement;
        private final int varpbit;
        private final InterfaceAddress address;

        Modern(int levelRequirement, int materialId, int varpbit) {
            this.levelRequirement = levelRequirement;
            this.varpbit = varpbit;
            this.address = new InterfaceAddress(() ->
                    Interfaces.getFirst(Prayers.GROUP_INDEX, a -> a.getMaterialId() == materialId, true)
            );
        }

        @Override
        public int getLevelRequirement() {
            return levelRequirement;
        }

        @Override
        public boolean isActivated() {
            return Varps.getBitValue(varpbit) > 0;
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }

        @Override
        public Book getBook() {
            return Book.MODERN;
        }
    }

    enum Ancient implements Prayer {

        PROTECT_ITEM(50, 26046, 16761),
        SAP_WARRIOR(50, 26023, 16762),
        SAP_RANGER(52, 26024, 16763),
        SAP_RANGE_STRENGTH(53, 26034, 16786),
        SAP_MAGE(54, 26026, 16764),
        SAP_MAGIC_STRENGTH(55, 26039, 16785),
        SAP_SPIRIT(56, 26025, 16765),
        SAP_DEFENCE(57, 26031, 16788),
        SAP_STRENGTH(58, 26032, 16787),
        BERSERKER(59, 26042, 16766),
        DEFLECT_SUMMONING(62, 26045, 16767),
        DEFLECT_MAGIC(65, 26041, 16768),
        DEFLECT_MISSILES(68, 26044, 16769),
        DEFLECT_MELEE(71, 26040, 16770),
        LEECH_ATTACK(74, 26027, 16771),
        LEECH_RANGED(76, 26028, 16772),
        LEECH_RANGE_STRENGTH(77, 26038, 16781),
        LEECH_MAGIC(78, 26030, 16773),
        LEECH_MAGIC_STRENGTH(79, 26043, 16782),
        LEECH_DEFENCE(80, 26035, 16774),
        LIGHT_FORM(80, 26048, 29066),
        DARK_FORM(80, 26049, 29067),
        LEECH_STRENGTH(82, 26036, 16775),
        LEECH_ENERGY(84, 26037, 16776),
        LEECH_ADRENALINE(86, 26029, 16777),
        CHRONICLE_ABSORPTION(87, 26053, 29070),
        SOUL_LINK(88, 26050, 29068),
        WRATH(89, 26022, 16778),
        TEAMWORK_PROTECTION(89, 26051, 29069),
        SUPERHEAT_FORM(91, 26055, 29071),
        SOUL_SPLIT(92, 26033, 16779),
        FORTITUDE(94, 26047, 29065),
        TURMOIL(95, 26019, 16780),
        ANGUISH(95, 26020, 16783),
        TORMENT(95, 26021, 16783),
        MALEVOLENCE(95, -1, -1),
        DESOLATION(95, -1, -1),
        AFFLICTION(95, -1, -1);

        private final int levelRequirement;
        private final int varpbit;
        private final InterfaceAddress address;

        Ancient(int levelRequirement, int materialId, int varpbit) {
            this.levelRequirement = levelRequirement;
            this.varpbit = varpbit;
            this.address = new InterfaceAddress(() ->
                    Interfaces.getFirst(Prayers.GROUP_INDEX, a -> a.getMaterialId() == materialId, true)
            );
        }

        @Override
        public int getLevelRequirement() {
            return levelRequirement;
        }

        @Override
        public boolean isActivated() {
            return Varps.getBitValue(varpbit) > 0;
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }

        @Override
        public Book getBook() {
            return Book.ANCIENT;
        }
    }
}

package org.rspeer.game.api.combat;

import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.Varps;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.InterfaceComposite;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.component.Item;
import org.rspeer.game.api.component.tab.Backpack;
import org.rspeer.game.api.component.tab.Skill;
import org.rspeer.game.api.component.tab.Skills;
import org.rspeer.game.providers.RSClient;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class Powers {

    private static final Pattern SHIELD_PATTERN = Pattern.compile(".*([Ww]ard|[Ss]hield).*");

    private Powers() {
        throw new IllegalAccessError();
    }

    public static int getAdrenalinePercentage() {
        return Varps.getValue(679) / 10;
    }

    public static boolean isDebuffActive() { // Quake and Guthix staff spec
        return Varps.getBitValue(35307) > 0;
    }

    public enum Magic implements Ability { // TODO Finish this

        DETONATE(-1, 1, 50, 2080),
        ASPHYXIATE(-1, 1, 50, 2099),
        METAMORPHOSIS(-1, 1, 100, 2081),
        SUNSHINE(-1, 1, 100, 18139);

        private final int levelRequirement;
        private final int adrenalineRequirement;
        private final int varpbit;

        private final InterfaceAddress address;

        Magic(int materialId, int levelRequirement, int adrenalineRequirement, int varpbit) {
            this.levelRequirement = levelRequirement;
            this.adrenalineRequirement = adrenalineRequirement;
            this.varpbit = varpbit;
            this.address = new InterfaceAddress(
                    () -> Interfaces.getFirst(InterfaceComposite.DEFENSIVE_ABILITIES.getGroup(),
                            a -> a.getMaterialId() == materialId, true)
            );
        }

        public static int getDetonateCharges() {
            return Varps.getBitValue(1897);
        }

        @Override
        public Skill getSkill() {
            return Skill.MAGIC;
        }

        @Override
        public int getAdrenalineRequirement() {
            return adrenalineRequirement;
        }

        @Override
        public int getLevelRequirement() {
            return levelRequirement;
        }

        @Override
        public int getVarpbit() {
            return varpbit;
        }

        @Override
        public boolean isShieldRequired() {
            return false;
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }
    }

    public enum Ranged implements Ability { // TODO Finish this

        SNIPE(-1, 1, 0, 2095),
        RAPID_FIRE(-1, 1, 50, 2096),
        DEATH_SWIFTNESS(-1, 1, 100, 18140);

        private final int levelRequirement;
        private final int adrenalineRequirement;
        private final int varpbit;

        private final InterfaceAddress address;

        Ranged(int materialId, int levelRequirement, int adrenalineRequirement, int varpbit) {
            this.levelRequirement = levelRequirement;
            this.adrenalineRequirement = adrenalineRequirement;
            this.varpbit = varpbit;
            this.address = new InterfaceAddress(
                    () -> Interfaces.getFirst(InterfaceComposite.DEFENSIVE_ABILITIES.getGroup(),
                            a -> a.getMaterialId() == materialId, true)
            );
        }

        public static int getSaltStacks() {
            return Varps.getBitValue(35697);
        }

        @Override
        public Skill getSkill() {
            return Skill.RANGED;
        }

        @Override
        public int getAdrenalineRequirement() {
            return adrenalineRequirement;
        }

        @Override
        public int getLevelRequirement() {
            return levelRequirement;
        }

        @Override
        public int getVarpbit() {
            return varpbit;
        }

        @Override
        public boolean isShieldRequired() {
            return false;
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }
    }

    public enum Defensive implements Ability {

        DEVOTION(21665, 1, 50, 21023, false),
        ANTICIPATION(14219, 3, 0, 2062, false),
        BASH(14224, 8, 0, -1, true),
        REVENGE(14227, 15, 50, 2069, false),
        PROVOKE(14221, 24, 0, -1, false),
        IMMORTALITY(14230, 29, 100, 2072, true),
        FREEDOM(14220, 34, 0, 2063, false),
        REFLECT(14225, 37, 50, 2067, true),
        RESONANCE(14222, 48, 0, 2065, true),
        REJUVENATE(14229, 52, 50, 2071, true),
        DEBILITATE(14226, 55, 50, 2068, false),
        PREPARATION(14223, 67, 0, 2066, true),
        BARRICADE(14228, 81, 100, 2070, true),
        NATURAL_INSTINCT(16549, 85, 100, 18141, false);

        private final int levelRequirement;
        private final int adrenalineRequirement;
        private final int varpbit;

        private final boolean shieldRequired;

        private final InterfaceAddress address;

        Defensive(int materialId, int levelRequirement, int adrenalineRequirement, int varpbit, boolean shieldRequired) {
            this.levelRequirement = levelRequirement;
            this.adrenalineRequirement = adrenalineRequirement;
            this.varpbit = varpbit;
            this.shieldRequired = shieldRequired;
            this.address = new InterfaceAddress(
                    () -> Interfaces.getFirst(InterfaceComposite.DEFENSIVE_ABILITIES.getGroup(),
                            a -> a.getMaterialId() == materialId, true)
            );
        }

        @Override
        public Skill getSkill() {
            return Skill.DEFENCE;
        }

        @Override
        public int getAdrenalineRequirement() {
            return adrenalineRequirement;
        }

        @Override
        public int getLevelRequirement() {
            return levelRequirement;
        }

        @Override
        public boolean isShieldRequired() {
            return shieldRequired;
        }

        @Override
        public int getVarpbit() {
            return varpbit;
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }
    }

    public enum Constitution implements Ability {

        REGENERATE(14267, 10, 0, 2061),
        SACRIFICE(21666, 10, 0, -1),
        TRANSFIGURE(26106, 10, 100, 2057),
        WEAPON_SPECIAL_ATTACK(26105, 10, 25, -1),
        SIPHON(18764, 20, 0, -1),
        INCITE(14268, 24, 100, 2098),
        TUSKAS_WRATH(14220, 34, 0, -1),
        GUTHIXS_BLESSING(16558, 85, 100, 35321),
        ONSLAUGHT(19289, 90, 100, 38922),
        ICE_ASYLUM(17503, 91, 100, -1);

        private final int levelRequirement;
        private final int adrenalineRequirement;
        private final int varpbit;

        private final InterfaceAddress address;

        Constitution(int materialId, int levelRequirement, int adrenalineRequirement, int varpbit) {
            this.levelRequirement = levelRequirement;
            this.adrenalineRequirement = adrenalineRequirement;
            this.varpbit = varpbit;
            this.address = new InterfaceAddress(() -> Interfaces.getFirst(InterfaceComposite.DEFENSIVE_ABILITIES.getGroup(),
                    a -> a.getMaterialId() == materialId, true)
            );
        }

        @Override
        public Skill getSkill() {
            return Skill.CONSTITUTION;
        }

        @Override
        public int getAdrenalineRequirement() {
            return adrenalineRequirement;
        }

        @Override
        public int getLevelRequirement() {
            return levelRequirement;
        }

        @Override
        public boolean isShieldRequired() {
            return false;
        }

        @Override
        public int getVarpbit() {
            return varpbit;
        }

        @Override
        public InterfaceAddress getAddress() {
            return address;
        }
    }

    public enum Spellbook {

        MODERN,
        ANCIENT,
        LUNAR,
        DUNGEONEERING;

        private static final int SPELLBOOK_INTERFACE = InterfaceComposite.SPELLBOOK.getGroup();

        public static Spellbook getCurrent() {
            return Spellbook.values()[Varps.getValue(4) & 0x3];
        }

        public static Spell getSelectedSpell() {
            RSClient client = Game.getClient();
            if (client == null || !client.isComponentSelected()) {
                return null;
            }
            for (Spell[] spells : Arrays.asList(Modern.values(), Ancient.values(), Lunar.values())) {
                for (Spell spell : spells) {
                    if (client.getSelectedComponentName().toLowerCase().contains(spell.getName()
                            .replace("_", " ").toLowerCase())) {
                        return spell;
                    }
                }
            }
            return null;
        }

        public static Spell getSelectedAutoCastSpell() {
            for (Spell[] spells : Arrays.asList(Modern.values(), Ancient.values(), Lunar.values())) {
                for (Spell spell : spells) {
                    if (spell.isAutoCasting()) {
                        return spell;
                    }
                }
            }
            return null;
        }

        public enum Modern implements Spell {

            EARTH_WAVE(14362, 65),
            AIR_SURGE(14364, 73),
            WATER_BOLT(14353, 27),
            STAGGER(14377),
            CHARGE_AIR_ORB(14373),
            LVL_5_ENCHANT(14386),
            WATCHTOWER_TELEPORT(14341),
            ENTANGLE(14394),
            WATER_WAVE(14361, 61),
            BONES_TO_BANANAS(14380),
            EARTH_SURGE(14366),
            CHARGE_EARTH_ORB(14375),
            FALADOR_TELEPORT(14337),
            LVL_3_ENCHANT(14384),
            APE_ATOLL_TELEPORT(14343),
            FIRE_BLAST(14359, 51),
            LVL_1_ENCHANT(14382),
            ARDOUGNE_TELEPORT(14340),
            VARROCK_TELEPORT(14336),
            BIND(14392),
            CONFUSE(14389),
            TELEPORT_BLOCK(14344),
            CURSE(14391),
            WEAKEN(14390),

            DISASSEMBLE(26543),
            WATER_SURGE(14365),
            ENCHANT_CROSSBOW_BOLT(14370),
            LOW_LEVEL_ALCHEMY(14378),
            FIRE_BOLT(14355, 33),
            AIR_BOLT(14352, 23),
            BONES_TO_PEACHES(14381),
            HOME_TELEPORT(14333),
            HIGH_LEVEL_ALCHEMY(14379),
            VULNERABILITY(14395),
            EARTH_BOLT(14354, 30),
            SNARE(14393),
            WATER_BLAST(14357, 40),
            SUPERHEAT_ITEM(14372),
            CHARGE_WATER_ORB(14374),

            CAMELOT_TELEPORT(14339),
            POLYPORE_STRIKE(14396),
            EARTH_BLAST(14358, 46),
            AIR_WAVE(14360, 58),
            STORM_OF_ARMADYL(14369),
            AIR_BLAST(14356, 37),
            FIRE_SURGE(14367),
            TELE_OTHER_FALADOR(14346),
            CHARGE_FIRE_ORB(14376),
            AIR_STRIKE(14348, 14),
            LVL_4_ENCHANT(14383),
            WATER_STRIKE(14349, 16),
            LUMBRIDGE_TELEPORT(14334),
            DIVINE_STORM(14369),

            MOBILISING_ARMIES_TELEPORT(14335),
            LVL_2_ENCHANT(14385),
            SLAYER_DART(14388),
            EARTH_STRIKE(14350, 18),
            GOD_WARS_DUNGEON_TELEPORT(14397),
            TELE_OTHER_LUMBRIDGE(14345),
            TELE_OTHER_CAMELOT(14347),
            FIRE_STRIKE(14351, 21),
            TROLLHEIM_TELEPORT(14342),
            FIRE_WAVE(14363, 68),
            ENFEEBLE(14371),

            TELEKINETIC_GRAB(14332),
            HOUSE_TELEPORT(14338),
            LVL_6_ENCHANT(14387);

            private final InterfaceAddress address;
            private final int varpVal;

            Modern(int materialId) {
                this(materialId, -1);
            }

            Modern(int materialId, int varpVal) {
                this.address = new InterfaceAddress(() ->
                        Interfaces.getFirst(SPELLBOOK_INTERFACE, a -> a.getMaterialId() == materialId, true)
                );
                this.varpVal = varpVal;
            }

            @Override
            public Spellbook getBook() {
                return Spellbook.MODERN;
            }

            @Override
            public String getName() {
                return name();
            }

            @Override
            public InterfaceAddress getAddress() {
                return address;
            }

            @Override
            public boolean isAutoCastable() {
                return varpVal != -1;
            }

            @Override
            public int getAutoCastVarpValue() {
                return varpVal;
            }
        }

        public enum Ancient implements Spell {

            CARRALLANGER_TELEPORT(25912),
            SMOKE_BURST(25916),
            SAPPHIRE_AURORA(25934),
            EMERALD_AURORA(25933),
            SMOKE_BARRAGE(25918),
            PRISM_OF_RESTORATION(25940),
            SENNTISTEN_TELEPORT(25908),
            PADDEWWA_TELEPORT(18765),
            CRYSTAL_MASK(25938),
            SHIELD_DOME(25931),
            SPELLBOOK_SWAP(14441),
            DAREEYAK_TELEPORT(25911),
            RUBY_AURORA(25936),
            PRISM_OF_LOYALTY(25941),

            ICE_BURST(25928),
            PRISM_OF_SALVATION(25942),
            ICE_BLITZ(25929),
            KHARYRLL_TELEPORT(25909),
            SHADOW_BURST(25920),
            SMOKE_RUSH(25915),
            SHADOW_RUSH(25919),
            BLOOD_BURST(25924),
            BLOOD_BLITZ(25925),
            ICE_BARRAGE(25930),
            POLYPORE_STRIKE(14396),
            RAPID_GROWTH(25946),
            BLOOD_RUSH(25923),
            GHORROCK_TELEPORT(25914),
            HOME_TELEPORT(14333),

            BLOOD_BARRAGE(25926),
            SHADOW_BLITZ(25921),
            SMOKE_BLITZ(25917),
            CRYSTALLISE(25939),
            OPAL_AURORA(25935),
            ICE_RUSH(25927),
            INTERCEPT(25932),
            SHADOW_BARRAGE(25922),

            LASSAR_TELEPORT(25910),
            PRISM_OF_DOWSING(25937),
            ANNAKARL_TELEPORT(25913);


            private final InterfaceAddress address;

            Ancient(int materialId) {
                this.address = new InterfaceAddress(() ->
                        Interfaces.getFirst(SPELLBOOK_INTERFACE, a -> a.getMaterialId() == materialId, true)
                );
            }

            @Override
            public Spellbook getBook() {
                return Spellbook.ANCIENT;
            }

            @Override
            public String getName() {
                return name();
            }

            @Override
            public InterfaceAddress getAddress() {
                return address;
            }

            @Override
            public boolean isAutoCastable() {
                return false;
            }

            @Override
            public int getAutoCastVarpValue() {
                return 0;
            }
        }

        public enum Lunar implements Spell {

            CURE_OTHER(14418),
            REMOTE_FARM(14447),
            CURE_ME(14421),
            BORROWED_POWER(14455),
            MAGIC_IMBUE(14411),
            HEAL_OTHER(14419),
            OURANIA_TELEPORT(14442),
            BOOST_POTION_SHARE(14410),
            TELE_GROUP_BARBARIAN(14430),
            BAKE_PIE(14402),
            HUNTER_KIT(14438),

            PLANK_MAKE(14440),
            FISHING_GUILD_TELEPORT(14414),
            WATERBIRTH_TELEPORT(14404),
            TELE_GROUP_MOONCLAN(14428),
            TELE_GROUP_ICE_PLATEAU(14434),
            ICE_PLATEAU_TELEPORT(14416),
            SOUTH_FALADOR_TELEPORT(14444),
            MONSTER_EXAMINE(14443),
            BARBARIAN_TELEPORT(14406),
            CURE_PLANT(14426),
            CATHERBY_TELEPORT(14415),
            DISRUPTION_SHIELD(14450),
            TROLLHEIM_TELEPORT(14453),
            TELE_GROUP_FISHING_GUILD(14432),
            TELEGROUP_KHAZARD(14431),
            SPIRITUALISE_FOOD(14449),
            TELE_GROUP_CATHERBY(14433),
            VENGEANCE(14423),

            CURE_GROUP(14424),
            SUPERGLASS_MAKE(14407),
            DREAM(14439),
            HEAL_GROUP(14425),
            SPELLBOOK_SWAP(14441),
            NORTH_ARDOUGNE_TELEPORT(1446),
            TELE_GROUP_TROLLHEIM(14454),
            TELE_GROUP_WATERBIRTH(14429),
            VENGEANCE_GROUP(14451),
            STRING_JEWELLERY(14409),
            MOONCLAN_TELEPORT(14403),
            TUNE_BANE_ORE(14452),
            HUMIDIFY(14437),
            NPC_CONTACT(14427),
            POLYPORE_STRIKE(14396),
            VENGEANCE_OTHER(14420),
            STAT_SPY(14435),
            STAT_RESTORE_POT_SHARE(14413),
            MAKE_LEATHER(14448),
            KHAZARD_TELEPORT(14408),

            REPAIR_RUNE_POUCH(14445),
            FERTILE_SOIL(14412),
            HOME_TELEPORT(14333);

            private final InterfaceAddress address;

            Lunar(int materialId) {
                this.address = new InterfaceAddress(() ->
                        Interfaces.getFirst(SPELLBOOK_INTERFACE, a -> a.getMaterialId() == materialId, true)
                );
            }

            @Override
            public Spellbook getBook() {
                return Spellbook.LUNAR;
            }

            @Override
            public String getName() {
                return name();
            }

            @Override
            public InterfaceAddress getAddress() {
                return address;
            }

            @Override
            public boolean isAutoCastable() {
                return false;
            }

            @Override
            public int getAutoCastVarpValue() {
                return 0;
            }
        }

        private interface Spell {

            Spellbook getBook();

            String getName();

            InterfaceAddress getAddress();

            boolean isAutoCastable();

            int getAutoCastVarpValue();

            default boolean isSelected() {
                RSClient client = Game.getClient();
                return client != null && client.isComponentSelected() && client.getSelectedComponentName()
                        .toLowerCase().contains(getName().replace("_", " ").toLowerCase());
            }

            default boolean activate() {
                InterfaceComponent component = getAddress().resolve();
                return component != null && component.interact(a -> a.equals("Cast") || a.equals("Activate"));
            }

            default boolean toggleAutoCast() {
                if (!isAutoCastable()) {
                    return false;
                }
                InterfaceComponent component = getAddress().resolve();
                return component != null && component.interact("Auto-cast");
            }

            default boolean isAutoCasting() {
                return isAutoCastable() && Varps.getBitValue(43) == getAutoCastVarpValue();
            }
        }
    }

    private interface Ability {

        Skill getSkill();

        int getAdrenalineRequirement();

        int getLevelRequirement();

        int getVarpbit();

        boolean isShieldRequired();

        InterfaceAddress getAddress();

        default boolean activate() {
            if (Skills.getLevel(getSkill()) < getLevelRequirement() || getAdrenalinePercentage() < getAdrenalineRequirement()) {
                return false;
            }
            Item item;
            if (isShieldRequired() && (item = Backpack.getFirst(SHIELD_PATTERN)) != null) {
                item.interact("Wear");
            } else {
                InterfaceComponent component = getAddress().resolve();
                return component != null && component.interact("Activate");
            }
            return false;
        }

        default boolean isActive() {
            return Varps.getBitValue(getVarpbit()) > 0;
        }

        default boolean isReady() { // Material id for ready abilities: 14659
            return false;
        }
    }
}

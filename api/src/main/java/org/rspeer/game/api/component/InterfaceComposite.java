package org.rspeer.game.api.component;

public enum InterfaceComposite {

    //Note: A lot of these have duplicates which are different in legacy interface

    LOBBY(906),
    LOBBY_BILLBOARDS(907),
    LOBBY_FRIENDS(910),
    LOBBY_CLAN_CHAT(912),
    LOBBY_OPTIONS(1513),
    TWITCH_CHAT_CHANNEL(228),
    TWITCH_BROADCAST(231),
    LOGIN_SCREEN(596),
    BANK(517),
    CLAN_WARS(789),
    TREASURE_HUNTER_POPUP(1252),
    TREASURE_HUNTER(1253),
    FRIENDS_CHAT_LIST(1427),
    GROUP_CHAT_LIST(1519),
    CLAN_CHAT_LIST(1110),
    OPTIONS_MENU(1433),
    QUICK_CHAT(1467),
    QUICK_CHAT_1(1470),
    QUICK_CHAT_2(1471),
    QUICK_CHAT_3(1472),
    QUICK_CHAT_4(1529),
    HUD(1477),
    PRODUCTION(1370),
    GUEST_CLAN_CHAT(1110),
    TOOL_SELECT(1179),
    PRODUCTION_PROGRESS(1251),
    SPELLBOOK(1461),
    PRAYER(1458),
    EQUIPMENT(1464),
    DEFENSIVE_ABILITIES(1883),
    RANGED_ABILITIES(1452),
    MELEE_ABILITIES(1460),
    BACKPACK(1473),
    EMOTES(590),
    FRIENDS_LIST(550),
    MAIN_ACTIONBAR(1430),
    SECOND_ACTIONBAR(1670),
    THIRD_ACTIONBAR(1671),
    FOURTH_ACTIONBAR(1672),
    FIFTH_ACTIONBAR(1673),
    SIXTH_ACTIONBAR(1674),
    GRAND_EXCHANGE(105),
    GRAND_EXCHANGE_INVENTORY(107),
    GRAND_EXCHANGE_COLLECT_ALL(651),
    DEPOSIT_BOX(11),
    BEAST_OF_BURDEN(662),
    AREA_LOOT(1622),
    CHAT_DIALOG_CONTINUE(1184),
    CHAT_DIALOG_OPTIONS(1188),
    SHOP(1265),
    CONFIRM_TRADE_SCREEN(334),
    INITIAL_TRADE_SCREEN(335),
    TRADE_ITEMS(336),
    ENTER_AMOUNT(1469),
    WORLD_HOP(1587),
    TELEPORT_OPTIONS(1578),
    CHATBOX(137),
    QUESTS(190),
    GAME_CLOCK(635),
    ACTIVE_TASK(1220),
    MUSIC_PLAYER(1416),
    NOTES(1417),
    RIBBON(1431),
    SUBRIBBON(1432),
    MINIMAP(1465),
    STATS(1466),
    GRAVE_INDICATOR(1483),
    BUFF_BAR(284),
    EFFECT_BAR(291),
    DEBUFF_BAR(1848),
    RUNEMETRICS(1588),
    UPGRADES_AND_EXTRAS(1607),
    EXPANDED_SLAYER_COUNTER(1639),
    AURA_MANAGEMENT(1929),
    ACTIVITY_TRACKER(1854),
    ACHIEVEMENT_PATHS(1894)
    ;

    private final int group;

    InterfaceComposite(int group) {
        this.group = group;
    }

    public static InterfaceComposite getByGroup(int group) {
        for (InterfaceComposite composite : InterfaceComposite.values()) {
            if (composite.group == group) {
                return composite;
            }
        }
        return null;
    }

    public int getGroup() {
        return group;
    }

    @Override
    public String toString() {
        String name = super.toString();
        return name.charAt(0) + name.substring(1).toLowerCase().replace("_", " ");
    }
}

package org.rspeer.game.api.component.tab;

import org.rspeer.game.api.Game;
import org.rspeer.game.api.component.InterfaceComposite;

public enum ActionWindow {

    ACTIVITY_TRACKER(8146, 32, InterfaceComposite.ACTIVITY_TRACKER),
    ACHIEVEMENT_PATHS(8146, 41, InterfaceComposite.ACHIEVEMENT_PATHS),
    WORN_EQUIPMENT(3, InterfaceComposite.EQUIPMENT),
    BACKPACK(2, InterfaceComposite.BACKPACK),
    SKILLS(0, InterfaceComposite.STATS),

    QUEST_LIST(8146, 31, InterfaceComposite.QUESTS),

    DEFENSIVE_ABILITIES(8, InterfaceComposite.DEFENSIVE_ABILITIES),
    RANGED_ABILITIES(7, InterfaceComposite.RANGED_ABILITIES),
    MELEE_ABILITIES(6, InterfaceComposite.MELEE_ABILITIES),
    MAGIC_BOOK(5, InterfaceComposite.SPELLBOOK),
    FAMILIAR(12, InterfaceComposite.BEAST_OF_BURDEN),
    PRAYER_ABILITIES(4, InterfaceComposite.PRAYER),

    GROUP_CHAT(25),
    GROUP_CHAT_LIST(27, InterfaceComposite.GROUP_CHAT_LIST),
    GUEST_CLAN_CHAT(22, InterfaceComposite.GUEST_CLAN_CHAT),
    TRADE_AND_ASSISTANCE(23),
    CLAN_CHAT(21),
    CLAN_CHAT_LIST(16, InterfaceComposite.CLAN_CHAT_LIST),
    FRIENDS_CHAT(20),
    FRIENDS_CHAT_LIST(15, InterfaceComposite.FRIENDS_CHAT_LIST),
    PRIVATE_CHAT(19),
    FRIENDS_LIST(14, InterfaceComposite.FRIENDS_LIST),
    ALL_CHAT(18),
    EMOTES(9, InterfaceComposite.EMOTES);

    final int script;
    final int argument;
    final InterfaceComposite[] groups;

    ActionWindow(int script, int argument, InterfaceComposite... groups) {
        this.script = script;
        this.argument = argument;
        this.groups = groups;
    }

    ActionWindow(int argument, InterfaceComposite... groups) {
        this(8161, argument, groups);
    }

    public enum State {
        CLOSED,
        LOADED,
        ACTIVE;
    }

    public void open() {
        Game.fireScriptEvent(script, argument);
    }
}

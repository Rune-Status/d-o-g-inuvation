package org.rspeer.ui;

import org.rspeer.Configuration;
import org.rspeer.api.commons.GameAccount;
import org.rspeer.io.HttpCommons;
import org.rspeer.bot.Bot;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.scene.Players;
import org.rspeer.rspeer_rest_api.BotPreferenceService;
import org.rspeer.rspeer_rest_api.Logger;
import org.rspeer.script.Script;

public final class BotTitlePaneHelper {

    private static final Object lock = new Object();
    private static String ip;

    public static String getFrameTitle(Bot bot, boolean withIp) {
        synchronized (lock) {
            try {
                StringBuilder builder = new StringBuilder(Configuration.APPLICATION_NAME);
                if(BotPreferenceService.getBoolean("showAccountOnMenuBar")) {
                    GameAccount account = Game.getDefaultAccount();
                    if(account != null) {
                        String rsn = Players.getLocal() != null ? Players.getLocal().getName() : "";
                        builder.append(" | ").append(account.getUsername());
                        if(rsn.length() > 0) {
                            builder.append(" ").append("(").append(rsn).append(")");
                        }
                    }
                }
                if(BotPreferenceService.getBoolean("showScriptOnMenuBar")) {
                    Script script = bot.getScriptController().getActiveScript();
                    if(script != null) {
                        builder.append(" | ").append(script.getMeta().name());
                    }
                }
                if(BotPreferenceService.getBoolean("allowScriptMessageOnMenuBar")) {
                    Script script = bot.getScriptController().getActiveScript();
                    if(script != null && script.getTitlePaneMessage() != null && script.getTitlePaneMessage().length() > 0) {
                        builder.append(" | ").append(script.getTitlePaneMessage());
                    }
                }
                if (!withIp) {
                    return builder.toString();
                }
                if (ip == null) {
                    ip = HttpCommons.getIpAddress();
                }
                builder.append(" | ").append(ip);
                return builder.toString();
            } catch (Exception e) {
                Logger.getInstance().capture(e);
                return Configuration.APPLICATION_NAME;
            }
        }
    }

    public static void refreshFrameTitle(Bot bot) {
        synchronized (lock) {
           boolean showIp = BotPreferenceService.getBoolean("showIpOnMenuBar", true);
           bot.getView().getFrame().setTitle(BotTitlePaneHelper.getFrameTitle(bot, showIp));
        }
    }
}
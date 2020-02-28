package org.rspeer.listeners;

import org.rspeer.api.commons.ExecutionService;
import org.rspeer.bot.Bot;
import org.rspeer.event.listeners.BotEventListener;
import org.rspeer.event.types.BotEvent;
import org.rspeer.ui.BotTitlePaneHelper;

public class DefaultBotMessageListener implements BotEventListener {

    private final Bot bot;

    public DefaultBotMessageListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void notify(BotEvent e) {
        System.out.println(e.getSource().getLeft() + " " + e.getSource().getRight().toString());
        String key = e.getSource().getLeft();
        if (key.equals("tile_pane_changed") && bot.getView() != null) {
            BotTitlePaneHelper.refreshFrameTitle(bot);
        }

        if (key.equals("bot_view_initialized")) {
            System.out.println("Starting bot.");
            ExecutionService.execute(bot::start, err -> bot.getView().getPanel().setError(e.toString()));
        }
    }
}

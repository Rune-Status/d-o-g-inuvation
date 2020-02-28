package org.rspeer.loader.task;

import org.rspeer.api.concurrent.Tasks;
import org.rspeer.bot.Bot;
import org.rspeer.bot.BotTask;
import org.rspeer.io.InnerPack;

public final class DecryptTask extends BotTask {

    public DecryptTask(Bot bot) {
        super(bot);
    }

    @Override
    public void run() throws Exception {
        Tasks.execute(new DownloadModscriptTask(bot, InnerPack.open(bot.getGameConfiguration())));
    }
}

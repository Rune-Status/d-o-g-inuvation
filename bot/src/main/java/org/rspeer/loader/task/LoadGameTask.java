package org.rspeer.loader.task;

import org.rspeer.api.concurrent.Tasks;
import org.rspeer.bot.Bot;
import org.rspeer.bot.BotTask;

public final class LoadGameTask extends BotTask {

    public LoadGameTask(Bot bot) {
        super(bot);
    }

    @Override
    public void run() {
        bot.getView().getPanel().setMessage(verbose());
        bot.getGameConfiguration().load();
        Tasks.execute(new DecryptTask(bot));
    }

    @Override
    public String verbose() {
        return "Downloading Modscript.";
    }
}

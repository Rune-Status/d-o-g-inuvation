package org.rspeer.bot;

import org.rspeer.api.concurrent.Task;

public abstract class BotTask implements Task {

    protected final Bot bot;

    protected BotTask(Bot bot) {
        this.bot = bot;
    }
}

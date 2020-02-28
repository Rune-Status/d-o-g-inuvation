package org.rspeer.loader.task;

import org.rspeer.Configuration;
import org.rspeer.arguments.Arguments;
import org.rspeer.arguments.QuickStartArgs;
import org.rspeer.arguments.QuickStartService;
import org.rspeer.bot.Bot;
import org.rspeer.bot.BotTask;
import org.rspeer.game.api.Game;
import org.rspeer.game.event.listener.EventListener;
import org.rspeer.instancing.ClientInfoAggregator;
import org.rspeer.listeners.DefaultRemoteMessageListener;
import org.rspeer.listeners.ServerConnectionListener;
import org.rspeer.rspeer_rest_api.PingService;
import org.rspeer.rspeer_rest_api.RSPeerApi;
import org.rspeer.rspeer_rest_api.RemoteMessageService;
import org.rspeer.script.ScriptController;
import org.rspeer.script.provider.LocalScriptProvider;
import org.rspeer.script.provider.RemoteScriptProvider;
import org.rspeer.script.provider.ScriptProvider;
import org.rspeer.script.provider.ScriptSource;
import org.rspeer.ui.BotTitlePaneHelper;

import java.io.File;

public final class GameLoadedTask extends BotTask {

    public GameLoadedTask(Bot bot) {
        super(bot);
    }

    @Override
    public void run() {
        RSPeerApi.initialize();
        PingService.getInstance().start(() -> new ClientInfoAggregator(this.bot).execute());
        setupListeners();
        BotTitlePaneHelper.refreshFrameTitle(this.bot);
        RemoteMessageService.poll();
        processArguments(bot.getArguments());
    }

    private void processArguments(Arguments arguments) {
        tryApplyScript(arguments);
        tryApplyQuickLaunch(arguments);
    }

    private void tryApplyScript(Arguments arguments) {
        if (arguments.getScript() == null) {
            return;
        }

        ScriptProvider<ScriptSource> local = new LocalScriptProvider(
                new File(Configuration.SCRIPTS), new File("scriptsout")
        );

        ScriptProvider<ScriptSource> remote = new RemoteScriptProvider();

        ScriptController controller = ScriptController.getInstance();
        if (controller.getActiveScript() == null) {
            for (ScriptSource src : local.load()) {
                if (src.getName().equalsIgnoreCase(arguments.getScript())) {
                    applyArgs(controller, src, arguments.getScriptArgs());
                }
            }

            for (ScriptSource src : remote.load()) {
                if (src.getName().equalsIgnoreCase(arguments.getScript())) {
                    applyArgs(controller, src, arguments.getScriptArgs());
                }
            }
        }
    }

    private void tryApplyQuickLaunch(Arguments arguments) {
        QuickStartArgs qs = arguments.getQuickLaunch();
        if(qs == null) {
            return;
        }
        QuickStartService service = new QuickStartService(qs);
        service.start();
    }

    private void applyArgs(ScriptController controller, ScriptSource src, String[] scriptArgs) {
        try {
            controller.setActiveScript(src.getTarget().newInstance());
            if (scriptArgs != null && scriptArgs.length > 0) {
                controller.setArgs(scriptArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        EventListener[] listeners = {
                new ServerConnectionListener(bot),
                new DefaultRemoteMessageListener()
        };

        for (EventListener listener : listeners) {
            Game.getEventDispatcher().register(listener);
        }
    }
}

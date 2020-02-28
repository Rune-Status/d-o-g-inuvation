package org.rspeer;

import org.rspeer.api.concurrent.Tasks;
import org.rspeer.arguments.Arguments;
import org.rspeer.bot.Bot;
import org.rspeer.bot.startup.SessionValidator;
import org.rspeer.event.EventDispatcherProvider;
import org.rspeer.event.impl.EventDispatcher;
import org.rspeer.event.types.BotEvent;
import org.rspeer.game.api.Definitions;
import org.rspeer.game.api.Game;
import org.rspeer.game.event.callback.ClientCallbackHandler;
import org.rspeer.game.event.callback.DefaultClientCallbackHandler;
import org.rspeer.internal.RuntimeExtensions;
import org.rspeer.io.LogFileBuffer;
import org.rspeer.listeners.DefaultBotMessageListener;
import org.rspeer.loader.GameConfiguration;
import org.rspeer.loader.GameEnvironment;
import org.rspeer.loader.task.LoadGameTask;
import org.rspeer.rspeer_rest_api.BotPreferenceService;
import org.rspeer.rspeer_rest_api.PingService;
import org.rspeer.rspeer_rest_api.RSPeerApi;
import org.rspeer.script.ScriptController;
import org.rspeer.ui.BotTitlePane;
import org.rspeer.ui.BotView;
import org.rspeer.ui.BotWindow;
import org.rspeer.ui.Login;
import org.rspeer.ui.skin.BotLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Inuvation implements Bot {

    private final GameConfiguration gameConfiguration;
    private final ScriptController scriptController;
    private final EventDispatcher eventDispatcher;
    private final ClientCallbackHandler clientCallback;
    private final Arguments arguments;

    private BotView view;
    private LogFileBuffer logFileBuffer;

    public Inuvation(String... args) {
        arguments = new Arguments(args);
        gameConfiguration = new GameEnvironment(arguments.getProxy());
        eventDispatcher = new EventDispatcher();
        EventDispatcherProvider.getInstance().setDispatcherOnce(eventDispatcher);
        clientCallback = new DefaultClientCallbackHandler(eventDispatcher, gameConfiguration.getProxy());
        scriptController = ScriptController.getInstance(this);
    }

    private static void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(Inuvation::shutdown));
    }

    public static void main(String[] args) {
        Bot bot = new Inuvation(args);

        ProxySelector.setDefault(new InuvationProxySelector(ProxySelector.getDefault()));
        System.setOut(new PrintStream(new ConsoleOutputStream(bot, System.out)));
        System.setErr(new PrintStream(new ConsoleOutputStream(bot, System.err)));

        bot.getEventDispatcher().register(new DefaultBotMessageListener(bot));
        bot.toggleFileLogging(BotPreferenceService.getBoolean("enableFileLogging"));

        addShutDownHook();

        EventQueue.invokeLater(() -> {
            try {
                applyLookAndFeel();
                bot.initializeView();
                BotTitlePane.decorate(bot.getView().getFrame());
                bot.getView().display();
                bot.getView().getPanel().setMessage("Initializing RSPeer Inuvation.");
                ScriptController scriptController = ScriptController.getInstance(bot);
                scriptController.setArgs(args);
                scriptController.addListener((e, e2) -> {
                    if (e2 != null) {
                        bot.getView().getMenuBar().scriptStarted();
                    } else {
                        bot.getView().getMenuBar().scriptStopped();
                    }
                });

                bot.getEventDispatcher().immediate(new BotEvent("bot_view_initialized", true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void applyLookAndFeel() {
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
            UIManager.put("IconButton", "javax.swing.plaf.basic.BasicButtonUI");
            UIManager.put("PopupMenu.consumeEventOnClose", Boolean.TRUE);
            //    UIManager.put(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);
            //  UIManager.put(SubstanceLookAndFeel.WINDOW_ROUNDED_CORNERS, Boolean.FALSE);
            //   UIManager.put(SubstanceLookAndFeel.USE_THEMED_DEFAULT_ICONS, Boolean.TRUE);
            UIManager.setLookAndFeel(new BotLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removeOldLogs() {
        Path logPath = Paths.get(Configuration.LOGS);
        try {
            List<Path> entries = Files.list(logPath)
                    .filter(x -> x.toFile().getName().contains("["))
                    .collect(Collectors.toList());
            for (Path entry : entries) {
                Files.delete(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        try {
            PingService.getInstance().onClientClose();
            RuntimeExtensions.shutdown();
        } catch (Throwable e) {
            RuntimeExtensions.shutdown();
        }
    }

    @Override
    public void toggleFileLogging(boolean enabled) {
        if (enabled) {
            logFileBuffer = new LogFileBuffer();
        } else if (logFileBuffer != null) {
            logFileBuffer.dipose();
            logFileBuffer = null;
        }
    }

    @Override
    public LogFileBuffer getLogFileBuffer() {
        return logFileBuffer;
    }

    @Override
    public void start() {
        try {
            RSPeerApi.setOnRetryCallback(event -> {
                if (event.getLastFailure() != null) {
                    event.getLastFailure().printStackTrace();
                }
                view.getPanel().setError("Failed to contact RSPeer Api.\nRetrying...\nAttempt: " + event.getAttemptCount());
            });

            EventQueue.invokeLater(() -> {
                SessionValidator validator = new SessionValidator(view, new Login());
                validator.execute(() -> Tasks.execute(new LoadGameTask(this)));
            });
        } catch (Throwable e) {
            view.getPanel().setError(e.toString());
        }
    }

    @Override
    public void initializeView() {
        if (view == null) {
            view = new BotWindow(this);
        }
    }

    @Override
    public GameConfiguration getGameConfiguration() {
        return gameConfiguration;
    }

    @Override
    public BotView getView() {
        return view;
    }

    @Override
    public ScriptController getScriptController() {
        return scriptController;
    }

    @Override
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Override
    public ClientCallbackHandler getCallbackHandler() {
        return clientCallback;
    }

    @Override
    public Arguments getArguments() {
        return arguments;
    }

    private static class ConsoleOutputStream extends OutputStream {

        private final Bot bot;
        private final PrintStream stream;

        private String str = "";

        private ConsoleOutputStream(Bot bot, PrintStream stream) {
            this.bot = bot;
            this.stream = stream;
        }

        @Override
        public void write(int b) {
            synchronized (this) {
                if ((char) b != '\n') {
                    str += (char) b;
                    stream.write(b);
                    return;
                }

                if (Definitions.isLoaded()) {
                    Game.getClient().printToConsole(str);
                }

                if (bot.getLogFileBuffer() != null) {
                    bot.getLogFileBuffer().add(str);
                }

                str = "";

                stream.write(b);
            }
        }
    }

    private static class InuvationProxySelector extends ProxySelector {

        private final ProxySelector delegate;

        private InuvationProxySelector(ProxySelector delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<Proxy> select(URI identifier) {
            return new ArrayList<>(delegate.select(identifier));
        }

        @Override
        public void connectFailed(URI identifier, SocketAddress address, IOException exception) {
            delegate.connectFailed(identifier, address, exception);
        }
    }
}

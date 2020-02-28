package org.rspeer.script;

import com.allatori.annotations.DoNotRename;
import org.rspeer.Configuration;
import org.rspeer.api.collections.Pair;
import org.rspeer.api.commons.GameAccount;
import org.rspeer.api.commons.Random;
import org.rspeer.api.commons.Time;
import org.rspeer.event.types.BotEvent;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.input.Keyboard;
import org.rspeer.rspeer_rest_api.BotPreferenceService;

import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class Script {

    private static final int[] ARROW_KEYS = new int[]{KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_RIGHT};

    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private State state = State.STOPPED;
    private boolean antiIdleActive = true;

    private GameAccount account;
    private String titlePaneMessage;

    public static Path getDataDirectory() {
        return Paths.get(Configuration.DATA);
    }

    /**
     * This method is invoked before the script starts looping.
     *
     * @param args Any script args (typically for quickstart)
     * @return {@code true} to continue onwards to the loop,
     * or {@code false} to prevent the script execution
     */
    @Deprecated
    public boolean onStart(String... args) {
        return true;
    }

    public boolean onStart() {
        return onStart(new String[0]);
    }

    public abstract int loop();

    public Class<? extends Script> getPassiveScript() {
        return null;
    }

    /**
     * Override this to listen for state changes in scripts
     */
    public void onState(State previous, State current) {

    }

    public void handleLoginError(String str) {
    }

    public final State getState() {
        return state;
    }

    public final void setState(State state) {
        onState(this.state, state);
        getController().notifyState(state);
        this.state = state;

        if (state == State.STOPPED) {
            service.shutdown();
        }
    }

    public final ScriptController getController() {
        return ScriptController.getInstance();
    }

    public final ScriptMeta getMeta() {
        return getClass().getAnnotation(ScriptMeta.class);
    }

    public final boolean start() {
        try {
            service.scheduleWithFixedDelay(() -> {
                if (antiIdleActive) {
                    System.out.println("[LoginService] Rotating camera");
                    int key = ARROW_KEYS[Random.nextInt(ARROW_KEYS.length - 1)];
                    Keyboard.dispatch(Keyboard.generateEvent(key, KeyEvent.KEY_PRESSED));
                    Time.sleep(Random.nextInt(100, 1000));
                    Keyboard.dispatch(Keyboard.generateEvent(key, KeyEvent.KEY_RELEASED));
                }
            }, 10, Random.nextInt(80, 150), TimeUnit.SECONDS);

            boolean start = onStart();
            setState(start ? State.RUNNING : State.STOPPED);
            if (start) {
                if (Game.isLoggedIn()) {
                    GameAccount defined = Game.getDefaultAccount();
                    if (defined != null) {
                        setAccount(defined);
                    } else {
                        setAccount(new GameAccount(Game.getClient().getUsername(), Game.getClient().getPassword()));
                    }
                }
                setState(State.RUNNING);
                return true;
            }
            service.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isAntiIdleActive() {
        return antiIdleActive;
    }

    public void setAntiIdleActive(boolean antiIdleActive) {
        this.antiIdleActive = antiIdleActive;
    }

    public GameAccount getAccount() {
        if (account != null) {
            return account;
        }
        return Game.getDefaultAccount();
    }

    public void setAccount(GameAccount account) {
        this.account = account;
    }

    public final String getTitlePaneMessage() {
        return titlePaneMessage;
    }

    public final void setTitlePaneMessage(String titlePaneMessage) {
        this.titlePaneMessage = titlePaneMessage;
        if (BotPreferenceService.getBoolean("allowScriptMessageOnMenuBar")) {
            Game.getEventDispatcher().immediate(new BotEvent(new Pair<>("tile_pane_changed", true)));
        }
    }

    public void processArgs(String... args) {

    }

    @DoNotRename
    public enum State {
        @DoNotRename STARTING,
        @DoNotRename RUNNING,
        @DoNotRename PAUSED,
        @DoNotRename STOPPED
    }
}

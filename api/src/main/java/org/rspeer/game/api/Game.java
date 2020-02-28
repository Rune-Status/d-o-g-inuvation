package org.rspeer.game.api;

import org.rspeer.api.commons.GameAccount;
import org.rspeer.api.commons.StopWatch;
import org.rspeer.event.impl.EventDispatcher;
import org.rspeer.game.providers.RSClient;
import org.rspeer.game.providers.RSPlayerFacade;
import org.rspeer.game.providers.RSScriptContext;

import java.util.HashMap;
import java.util.Map;

public final class Game {

    private static final Map<String, Integer> CONSTANTS = new HashMap<>();

    private static RSClient client;
    private static GameAccount defaultAccount;
    private static StopWatch runtime;
    private static boolean flagException = true;

    private Game() {
        throw new IllegalAccessError();
    }

    /**
     * @return The current game state from the listings in {@link State}.
     * If it is not listed in the enums it is not hooked. Note that working with the raw
     * {@link RSClient#getConnectionState()} values is unrecommended due to the game
     * scrambling values over updates
     */
    public static State getState() {
        int val = getClient().getConnectionState();
        for (State state : State.values()) {
            String key = state.key;
            if (key.endsWith("_")) {
                int count = getClient().getConstant(key + "COUNT");
                for (int i = 0; i < count; i++) {
                    int cst = getClient().getConstant(key + i);
                    if (val == cst) {
                        return state;
                    }
                }
            } else {
                int cst = getClient().getConstant(key);
                if (val == cst) {
                    return state;
                }
            }
        }
        return null;
    }

    public enum State {

        LOGGED_IN("CONNECTION_STATE_LOGGED_IN"),
        WORLD_HOPPING("CONNECTION_STATE_WORLD_HOPPING"),
        LOADING_MAP("CONNECTION_STATE_LOADING_MAP"),
        LOGGED_OUT("CONNECTION_STATE_LOGGED_OUT_"),
        LOADING("CONNECTION_STATE_LOADING_"),
        LOBBY("CONNECTION_STATE_LOBBY_");

        private final String key;

        State(String key) {
            this.key = key;
        }
    }

    /**
     * @return {@code true} if you are logged in. Note that this method also returns false for the lobby
     */
    public static boolean isLoggedIn() {
        return getState() == State.LOGGED_IN;
    }

    public static RSPlayerFacade getPlayerFacade() {
        return getClient().getPlayerFacade();
    }

    public static RSClient getClient() {
        return client;
    }

    public static void setClient(RSClient client) {
        if (Game.client != null) {
            throw new IllegalStateException("Client already loaded?");
        }
        Game.client = client;
        Game.runtime = StopWatch.start();
    }

    /**
     * Nulls the client, this is for internal use to reload the game when your session expires after 23 hours
     */
    public static void removeClient() {
        Game.client = null;
    }

    /**
     * Executes a clientscript with the given id and arguments
     */
    public static void fireScriptEvent(int id, Object... args) {
        Object[] idInclusiveArgs = new Object[args.length + 1];
        System.arraycopy(args, 0, idInclusiveArgs, 1, args.length);
        idInclusiveArgs[0] = id;
        RSScriptContext scriptContext = client.createScriptContext();
        scriptContext.setArgs(idInclusiveArgs);
        getClient().scriptInvoked(scriptContext, 500000);
    }

    public static int getEngineCycle() {
        return getClient().getEngineCycle();
    }

    public static int getCutsceneId() {
        return getClient().getCutsceneId();
    }

    public static int getCutsceneState() {
        return getClient().getCutsceneState();
    }

    public static EventDispatcher getEventDispatcher() {
        return getClient().getEventDispatcher();
    }

    /**
     * @return A default account set via {@link #setDefaultAccount(GameAccount)}. This is also used by quickstart
     */
    public static GameAccount getDefaultAccount() {
        return defaultAccount;
    }

    public static void setDefaultAccount(GameAccount account) {
        Game.defaultAccount = account;
    }

    public static void logout() {
        Game.getClient().getActiveConnection().close();
    }

    public static void setConstant(String key, int val) {
        CONSTANTS.put(key, val);
    }

    public static int getConstant(String key) {
        return CONSTANTS.get(key);
    }

    /**
     * @return The uptime of the running game session
     */
    public static StopWatch getRuntime() {
        return runtime;
    }

    /**
     * For internal use only
     */
    public static boolean isFlagException() {
        return flagException;
    }

    /**
     * For internal use only
     */
    public static void setFlagException(boolean flagException) {
        Game.flagException = flagException;
    }
}

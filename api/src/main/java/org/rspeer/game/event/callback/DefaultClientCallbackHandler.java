package org.rspeer.game.event.callback;

import org.rspeer.api.commons.Functions;
import org.rspeer.api.commons.Time;
import org.rspeer.event.impl.EventDispatcher;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.adapter.scene.Mobile;
import org.rspeer.game.adapter.scene.SceneObject;
import org.rspeer.game.api.Definitions;
import org.rspeer.game.api.Game;
import org.rspeer.game.api.action.ActionProcessor;
import org.rspeer.game.api.action.tree.Action;
import org.rspeer.game.api.action.tree.ObjectAction;
import org.rspeer.game.api.commons.ArrayUtils;
import org.rspeer.game.api.component.InterfaceAddress;
import org.rspeer.game.api.component.Interfaces;
import org.rspeer.game.api.component.tab.Skill;
import org.rspeer.game.api.scene.Projection;
import org.rspeer.game.event.types.*;
import org.rspeer.game.providers.*;
import org.rspeer.loader.ProxyConfig;

import java.awt.*;

public final class DefaultClientCallbackHandler extends ClientCallbackHandler {

    private static final InterfaceAddress DISPLAY_ADDRESS = new InterfaceAddress(
            () -> Interfaces.getFirst(e -> Math.abs(e.getContentType()) == 1337)
    );

    private static final String[] BANNED_INTERACTABLE_OBJECTS = {"Corrupted Scarab"};

    public static byte[] RANDOM_DAT;
    private final EventDispatcher dispatcher;
    private final CommandProcessor commands;
    private ProxyConfig proxyConfig;

    public DefaultClientCallbackHandler(EventDispatcher dispatcher,
            ProxyConfig proxyConfig,
            CommandProcessor commands) {
        this.dispatcher = dispatcher;
        this.proxyConfig = proxyConfig;
        this.commands = commands;
    }

    public DefaultClientCallbackHandler(EventDispatcher dispatcher, ProxyConfig proxyConfig) {
        this(dispatcher, proxyConfig, new DefaultCommandProcessor());
    }

    @Override
    public boolean notifyConnectionState(String context, int previous, int current) {
        if (previous != current) {
            norenderCache = !Projection.isRenderingDisabled();
            ConnectionStateEvent event = new ConnectionStateEvent(previous, current, context);
            dispatcher.immediate(event);
        }
        return false;
    }

    @Override
    public boolean notifyLoginResponse(String context, int previous, int current) {
        if (previous != current) {
            LoginResponseEvent event = new LoginResponseEvent(LoginResponseEvent.Type.LOGIN, context, previous, current);
            dispatcher.immediate(event);
        }
        return false;
    }

    @Override
    public boolean notifyLobbyResponse(String context, int previous, int current) {
        if (previous != current) {
            LoginResponseEvent event = new LoginResponseEvent(LoginResponseEvent.Type.LOBBY, context, previous, current);
            dispatcher.immediate(event);
        }
        return false;
    }

    @Override
    public boolean notifyInterfaceComponentRenderCycle(String context, RSInterfaceComponent component, int previous, int current) {
        return false;
    }

    public boolean processConsoleCommand(String cmd, boolean bool1, boolean bool2) {
        System.out.println(cmd);
        return commands.accept(cmd);
    }

    @Override
    public void itemTableUpdated(int table, int slot, int id, int quantity, boolean idk) {
        long key = (long) (table | (idk ? Integer.MIN_VALUE : 0));
        if (Game.getClient().getItemTables() != null) {
            RSItemTable inv = Game.getClient().getItemTables().getSynthetic(key);
            int oldId = -1;
            int oldQuantity = 0;
            if (inv != null && slot < inv.getIds().length) {
                oldId = inv.getIds()[slot];
                oldQuantity = inv.getQuantities()[slot];
            }
            TableItemEvent event = new TableItemEvent(table, idk, slot, id, quantity, oldId, oldQuantity);
            dispatcher.immediate(event);
        }
    }

    @Override
    public void notifyPlayers(int idx, RSPlayer previous, RSPlayer current) {

    }

    private boolean norenderCache = Projection.isRenderingDisabled();

    @Override
    public void onEngineTick() {
        Definitions.populate();
        ActionProcessor.flush();

        boolean norender = Projection.isRenderingDisabled();
        if (norender != norenderCache) {
            InterfaceComponent display = DISPLAY_ADDRESS.resolve();
            if (display != null) {
                norenderCache = norender;
                display.getProvider().setContentType(norender ? -1337 : 1337);
            }
        }

        EngineTickEvent event = new EngineTickEvent();
        dispatcher.immediate(event);
    }

    @Override
    public boolean processAction(RSMenuItem item, int crosshairX, int crosshairY, boolean something) {
        Action action = Action.valueOf(item);
        MenuActionEvent event = new MenuActionEvent(item.getOpcode(), item.getArg0(),
                item.getArg1(), item.getArg2(), item.getActionText(),
                item.getTargetText(), action
        );
        dispatcher.immediate(event);

        if (isDebugMenuActions()) {
            System.out.println(event.toString());
        }

        if (action instanceof ObjectAction) {
            SceneObject obj = ((ObjectAction) action).getSource();
            if (obj != null && ArrayUtils.contains(BANNED_INTERACTABLE_OBJECTS, obj.getName())) {
                event.consume();
            }
        }
        return event.isConsumed();
    }

    @Override
    public void scriptInvoked(RSScriptContext ctx, int idk) {
        RuneScriptEvent event = new RuneScriptEvent(ctx);
        dispatcher.immediate(event);
    }

    @Override
    public void entityHovered(RSTestHook test, int mouseX, int mouseY) {
        if (test.getNode() != null) {
            EntityHoverEvent event = new EntityHoverEvent(test.getNode(), new Point(mouseX, mouseY));
            dispatcher.immediate(event);
        }
    }

    @Override
    public boolean shouldRenderComponent(RSInterfaceComponent component) {
        RSClient client = Game.getClient();
        //if (component.getAdapter().getParentIndex() == 1724) return true;
        return client.shouldRenderComponent(component);
    }

    @Override
    public byte[] getRandomDat(byte[] bytes) {
        if (RANDOM_DAT != null) {
            return RANDOM_DAT;
        }
        return bytes;
    }

    @Override
    public void onRenderTick(RSClient lol) {
        if (Game.isLoggedIn() && Projection.isEngineTickDelayEnabled()) {
            int delay = Projection.getEngineTickDelay();
            if (delay > 0) {
                Time.sleep(Projection.getEngineTickDelay());
            }
        }
    }

    @Override
    public void onGEOfferChanged(RSGrandExchangeOffer offer) {
        GrandExchangeOfferEvent event = new GrandExchangeOfferEvent(offer);
        dispatcher.immediate(event);
    }

    @Override
    public boolean notifyBufferedConnectionIdleTicks(String str, RSBufferedConnection connection, int prev, int curr) {
        return true;
    }

    @Override
    public boolean notifyAnimatorAnimation(String str, RSAnimator connection, RSAnimation prev, RSAnimation curr) {
        if (connection.getOwner() != null) {
            Mobile mobile = connection.getOwner().getAdapter();
            int previous = Functions.mapOrM1(() -> prev, RSAnimation::getId);
            int currrent = Functions.mapOrM1(() -> curr, RSAnimation::getId);

            dispatcher.immediate(new AnimationEvent(mobile, str, previous, currrent));
            dispatcher.immediate(new AnimationChangedEvent(mobile, str, previous, currrent));
        }
        return false;
    }

    @Override
    public void add(RSNodeDeque deque, RSNode node) {
        if (Game.getClient().getProjectileNodeDeque() == deque) {
            dispatcher.immediate(new ProjectileSpawnedEvent(((RSProjectileNode) node).getProjectile().getAdapter()));
        }
    }

    @Override
    public void notifySkill(RSSkill skill, SkillEvent.Type type, int previous, int current) {
        if (previous == current) {
            return;
        }

        try {
            Skill parsed = Skill.values()[skill.getLevelData().getIndex()];
            SkillEvent event = new SkillEvent(parsed, type, previous, current);
            dispatcher.immediate(event);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void messageReceived(int type, int var1, String source, String rawSource, String rawSource2,
            String message, String channel, int unknown, RSChatMessageIcon icon) {
        if (rawSource != null) {
            MessageEvent event = new MessageEvent(type, rawSource, message, channel, icon);
            dispatcher.immediate(event);
        }
    }
}

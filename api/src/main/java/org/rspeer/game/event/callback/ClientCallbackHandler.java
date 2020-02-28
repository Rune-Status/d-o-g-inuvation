package org.rspeer.game.event.callback;

import org.rspeer.game.event.types.SkillEvent;
import org.rspeer.game.providers.*;

public abstract class ClientCallbackHandler {

    private boolean debugMenuActions;

    public abstract boolean notifyConnectionState(String context, int previous, int current);

    public abstract boolean notifyLoginResponse(String context, int previous, int current);

    public abstract boolean notifyLobbyResponse(String context, int previous, int current);

    public abstract boolean notifyInterfaceComponentRenderCycle(String context, RSInterfaceComponent component, int previous, int current);

    public abstract void notifyPlayers(int idx, RSPlayer previous, RSPlayer current);

    public abstract void onEngineTick();

    public abstract boolean processAction(RSMenuItem item, int x, int y, boolean something);

    public abstract void messageReceived(int type, int var1, String source, String rawSource,
            String rawSource2, String message,
            String channel, int unknown, RSChatMessageIcon icon);

    public abstract boolean processConsoleCommand(String command, boolean bool1, boolean bool2);

    public abstract void itemTableUpdated(int table, int slot, int id, int quantity, boolean idk);

    public abstract void scriptInvoked(RSScriptContext ctx, int idx);

    public abstract void entityHovered(RSTestHook test, int mouseX, int mouseY);

    public abstract boolean shouldRenderComponent(RSInterfaceComponent component);

    public abstract byte[] getRandomDat(byte[] bytes);

    public abstract void onRenderTick(RSClient lol);

    public abstract void onGEOfferChanged(RSGrandExchangeOffer offer);

    public abstract boolean notifyBufferedConnectionIdleTicks(String str, RSBufferedConnection connection, int prev, int curr);

    public abstract boolean notifyAnimatorAnimation(String str, RSAnimator connection, RSAnimation prev, RSAnimation curr);

    public abstract void add(RSNodeDeque deque, RSNode node);

    public final boolean notifySkillLevel(String ctx, RSSkill skill, int previous, int current) {
        notifySkill(skill, SkillEvent.Type.LEVEL, previous, current);
        return false;
    }

    public final boolean notifySkillCurrentLevel(String ctx, RSSkill skill, int previous, int current) {
        notifySkill(skill, SkillEvent.Type.TEMPORARY_LEVEL, previous, current);
        return false;
    }

    public final boolean notifySkillExperience(String ctx, RSSkill skill, int previous, int current) {
        notifySkill(skill, SkillEvent.Type.EXPERIENCE, previous, current);
        return false;
    }

    public abstract void notifySkill(RSSkill skill, SkillEvent.Type type, int previous, int current);

    public final boolean isDebugMenuActions() {
        return debugMenuActions;
    }

    public final void setDebugMenuActions(boolean debugMenuActions) {
        this.debugMenuActions = debugMenuActions;
    }
}

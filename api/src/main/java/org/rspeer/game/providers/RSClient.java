package org.rspeer.game.providers;

import org.rspeer.api.commons.Functions;
import org.rspeer.event.impl.EventDispatcher;
import org.rspeer.game.adapter.cache.ItemDefinition;
import org.rspeer.game.api.Game;
import org.rspeer.game.event.callback.ClientCallbackHandler;

import java.awt.*;
import java.math.BigInteger;

public interface RSClient extends RSLoader {

    static boolean processException(Throwable t) {
        if (Game.isFlagException()) {
            Game.setFlagException(false);
            return true;
        }
        return false;
    }

    boolean[] getValidInterfaces();

    RSClanChat getClanChat();

    RSClanChat getClanChat1();

    RSJs5ConfigGroup getVarpBitConfigGroup();

    RSJs5ConfigGroup getItemDefinitionConfigGroup();

    RSNodeTable<RSItemTable> getItemTables();

    RSNodeTable<RSAnimableObjectNode> getAnimableObjectNodes();

    RSScene getScene();

    RSNodeTable<RSGroundItemDeque> getGroundItemDeques();

    RSEnumDefinitionLoader getEnumDefinitionLoader();

    RSDefinitionCacheLoader<RSItemDefinition> getItemDefinitionLoader();

    int getCutsceneId();

    String getPassword();

    long getRefreshRate();

    int getMapState();

    RSJs5ConfigGroup getAnimationConfigGroup();

    int getEngineCycle();

    RSNodeDeque getProjectileNodeDeque();

    RSGrandExchangeOffer[][] getGrandExchangeOffers();

    Image getImage();

    Canvas getCanvas();

    RSBufferedConnection[] getBufferedConnections();

    RSBufferedConnection getActiveConnection();

    RSPlayer[] getPlayers();

    RSGlobalPlayer[] getGlobalPlayers();

    int getLoginState();

    int getLoginResponse();

    int getLobbyResponse();

    int getConnectionState();

    RSExpandableMenuItem getExpandedMenuItem();

    RSNodeDeque getMenuItemDeque();

    RSFriendsChatMember[] getFriendsChatMembers();

    int getMenuWidth();

    int getMapScale();

    RSObjectNode[] getNpcObjectNodes();

    int getMenuHeight();

    int getPlayerRights();

    RSJs5ConfigGroup getQuestConfigGroup();

    int getExpandedMenuY();

    int getExpandedMenuX();

    RSJs5ConfigGroup getItemTableConfigGroup();

    RSWorld[] getWorlds();

    int getMapOffset();

    int getExpandedMenuHeight();

    int getMenuRowCount();

    boolean isMenuOpen();

    RSRenderConfiguration getActiveRenderConfiguration();

    RSNetResourceWorker getNetResourceWorker();

    RSNodeTable<RSInterfaceNode> getInterfaceNodes();

    BigInteger getLoginRsaExponent();

    float getMapAngle();

    RSCameraImpl getCamera();

    RSGuidanceArrow[] getGuidanceArrows();

    RSOverheadMessage[] getOverheadMessages();

    RSPlayer getPlayer();

    RSJs5ConfigGroup getObjectDefinitionConfigGroup();

    RSInterface[] getInterfaces();

    RSInterfaceComponent getDialogProcessingComponent();

    int getExpandedMenuWidth();

    Rectangle[] getInterfaceBounds();

    RSNodeTable<RSObjectNode> getNpcObjectNodeTable();

    RSJs5ConfigGroup getNpcDefinitionConfigGroup();

    BigInteger getLoginRsaModulus();

    RSQuestDefinitionLoader getQuestDefinitionLoader();

    RSItemTableDefinitionLoader getItemTableDefinitionLoader();

    RSPlayerFacade getPlayerFacade();

    int getCutsceneState();

    RSInterfaceComponent[] getMainComponents();

    int getMenuY();

    int getMenuX();

    RSArchiveResourceProvider[] getArchiveResourceProviders();

    RSNodeTable getExpandableMenuItems();

    String getUsername();

    String[] getPlayerActions();

    String getSelectedComponentAction();

    String getSelectedComponentName();

    short[] getPlayerActionsIndexOffset();

    int getSelectedComponentAttribute();

    int getSelectedComponentUid();

    boolean isComponentSelected();

    void printToConsole(String mssg);

    void scriptInvoked(RSScriptContext ctx, int len);

    void setConnectionState(int state);

    boolean setWorld(int i1, String s1, int i2, int i3);

    void constructDialogPacket(int sec, int tri);

    int getConstant(String val);

    RSScriptContext createScriptContext();

    boolean shouldRenderComponent(RSInterfaceComponent component);

    default RSInterfaceComponentDefinition loadComponentDefinition2(RSInterfaceComponent iface) {
        RSInterfaceComponentDefinition var1 = getComponentDefinitionNodeTable().getSynthetic(((long) iface.getParentUid() << 32) + (long) iface.getComponentIndex());
        return null != var1 ? var1 : iface.getDefaultDefinition();
    }


    RSDefinitionCacheLoader<RSParameterDefinition> getParameterDefinitionLoader();

    EventDispatcher getEventDispatcher();

    void setEventDispatcher(EventDispatcher dispatcher);

    RSNodeTable<RSInterfaceComponentDefinition> getComponentDefinitionNodeTable();

    ClientCallbackHandler getCallbackHandler();

    void setCallbackHandler(ClientCallbackHandler handler);

    default String getComponentUseAction2(RSInterfaceComponent iface) {
        RSInterfaceComponentDefinition definition = loadComponentDefinition2(iface);
        String useAction = iface.getUseAction();
        if (definition.getApplicationTargets() == 0) {
            return null;
        } else if (useAction != null && useAction.trim().length() != 0) {
            return useAction;
        }
        return null;
    }

    RSMenuItem createMenuItem(int type, int op, long primary, int secondary, int tertiary);

    void processAction(RSMenuItem var1, int var2, int var3, boolean var4);

    long getObjectUid(RSSceneObject object, int x, int y);

    default RSParameterDefinition loadParameterDefinition(int uid) {
        return Functions.mapOrNull(this::getParameterDefinitionLoader, e -> e.load(uid));
    }

    default RSInterfaceComponentDefinition getComponentDefinition(int uid) {
        return Functions.mapOrNull(this::getComponentDefinitionNodeTable, e -> e.getSynthetic(uid));
    }

    default ItemDefinition getItemDefinition(int id) {
        RSDefinitionCacheLoader<RSItemDefinition> defLoader = getItemDefinitionLoader();
        if (defLoader != null) {
            RSItemDefinition definition = defLoader.load(id);
            if (definition != null) {
                return definition.getAdapter();
            }
        }
        return null;
    }

    default RSObjectDefinition getObjectDefinition(int id) {
        return Functions.mapOrDefault(this::getScene, x -> x.getObjectDefinition(id), null);
    }

    default void worldToScreen(float x, float y, float height, float[] dest) {
        RSRenderConfiguration cfg = getActiveRenderConfiguration();
        if (cfg != null) {
            cfg.worldToScreen(x, y, height, dest);
        }
    }

    int getDestinationX();

    int getDestinationY();
}
/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.ConstantHook;
import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.hookspec.hook.Hook;
import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.Opcodes;
import org.objectweb.casm.Type;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.query.InsnQuery;
import org.objectweb.casm.commons.cfg.query.MemberQuery;
import org.objectweb.casm.commons.cfg.query.NumberQuery;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.*;
import org.objectweb.casm.tree.*;
import org.rspeer.api.collections.Multiset;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Dogerina
 * @since 28-06-2015
 */
@VisitorInfo(hooks = {"interfaceBounds", "mapOffset", "mapAngle", "mapScale", "mapState", "menuX", "menuY",
        "menuWidth", "menuHeight", "guidanceArrows", "processAction", "worlds", "playerFacade", "player", "players",
        "npcObjectNodes", "canvas", "scene", "friendsChatMembers", "itemDefinitionConfigGroup", "objectDefinitionConfigGroup",
        "npcDefinitionConfigGroup", "itemTableConfigGroup", "questConfigGroup", "animationConfigGroup", "varpBitConfigGroup",
        "connectionState", "setConnectionState", "engineCycle", "activeRenderConfiguration",
        "expandedMenuX", "expandedMenuY", "expandedMenuWidth", "expandedMenuHeight", "expandableMenuItems",
        "expandedMenuItem", "menuOpen", "menuItemDeque", "menuRowCount", "camera", "projectileNodeDeque",
        "itemTables", "grandExchangeOffers", "overheadMessages", "username", "password", "groundItemDeques",
        "interfaces", "mainComponents", "cutsceneState", "cutsceneId", "itemDefinitionLoader",
        "addMenuItem", "loop", "bufferedConnections", "getActiveConnection", "getObjectUid", "animableObjectNodes",
        "npcObjectNodeTable", "loginRsaExponent", "loginRsaModulus", "createCredentialsBuffer", "questDefinitionLoader",
        "interfaceNodes", "newScriptContext", "gc", "archiveResourceProviders", "playerRights", "processConsoleCommand",
        "printToConsole", "image", "netResourceWorker", "refreshRate", /*"destinationX", "destinationY", "playerIndex"*/
        "dialogProcessingComponent", "loadComponentDefinition", "parameterDefinitionLoader", "componentSelected",
        "selectedComponentName", "selectedComponentAction", "selectedComponentAttribute", "selectedComponentUid",
        "getComponentUseAction", "itemTableDefinitionLoader", "componentDefinitionNodeTable", "messageReceived",
        "playerActions", "playerActionsIndexOffset", "scriptInvoked", "itemTableUpdated", "destinationX", "destinationY",
        "shouldRenderComponent", "getRandomDat", "constructDialogPacket", "onRenderTick", "setWorld",
        "CONNECTION_STATE_WORLD_HOPPING", "CONNECTION_STATE_LOGGED_IN",
        "CONNECTION_STATE_LOADING_MAP", "CONNECTION_STATE_LOBBY_COUNT", "CONNECTION_STATE_LOADING_COUNT",
        "CONNECTION_STATE_LOGGED_OUT_COUNT", "globalPlayers", "decodeGlobalPlayer", "gpiBaseX", "gpiBaseY",
        "loginState", "loginResponse", "lobbyResponse", "enumDefinitionLoader", "enumConfigGroup",
        "clanChat", "clanChat1", "validInterfaces",

        "CONNECTION_STATE_LOGGED_OUT_8", "CONNECTION_STATE_LOGGED_OUT_7",
        "CONNECTION_STATE_LOGGED_OUT_6", "CONNECTION_STATE_LOGGED_OUT_5", "CONNECTION_STATE_LOGGED_OUT_4",
        "CONNECTION_STATE_LOGGED_OUT_3", "CONNECTION_STATE_LOGGED_OUT_2", "CONNECTION_STATE_LOGGED_OUT_1",
        "CONNECTION_STATE_LOGGED_OUT_0", "CONNECTION_STATE_LOADING_1", "CONNECTION_STATE_LOADING_0",
        "CONNECTION_STATE_LOBBY_5", "CONNECTION_STATE_LOBBY_4", "CONNECTION_STATE_LOBBY_3", "CONNECTION_STATE_LOBBY_2",
        "CONNECTION_STATE_LOBBY_1", "CONNECTION_STATE_LOBBY_0", "CONNECTION_STATE_LOADING_3",
        "CONNECTION_STATE_LOADING_2", "CONNECTION_STATE_LOADING_5", "CONNECTION_STATE_LOADING_4"
})
public class Client extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.name.equalsIgnoreCase("client"); //in case we run on a refactored pack
    }

    @Override
    public void visit() {
        FieldHook hook = updater.visitor("Client").getFieldHook("itemTableConfigGroup");
        for (ClassNode cn : updater.classnodes.values()) {
            loop:
            for (MethodNode mn : cn.methods) {
                if (mn.name.equals("<init>")) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain instanceof FieldInsnNode) {
                            FieldInsnNode fin = (FieldInsnNode) ain;
                            if (hook != null && fin.owner.equals(hook.clazz) && fin.name.equals(hook.field)) {
                                updater.visitor("ItemTableDefinitionLoader").cn = cn;
                                break loop;
                            }
                        }
                    }
                }
            }
        }

        hook = updater.visitor("Client").getFieldHook("enumConfigGroup");
        for (ClassNode cn : updater.classnodes.values()) {
            loop:
            for (MethodNode mn : cn.methods) {
                if (mn.name.equals("<init>")) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain instanceof FieldInsnNode) {
                            FieldInsnNode fin = (FieldInsnNode) ain;
                            if (hook != null && fin.owner.equals(hook.clazz) && fin.name.equals(hook.field)) {
                                updater.visitor("EnumDefinitionLoader").cn = cn;
                                break loop;
                            }
                        }
                    }
                }
            }
        }

        for (ClassNode cn : updater.classnodes.values()) {
            for (FieldNode fn : cn.fields) {
                if ((fn.access & ACC_STATIC) == 0) {
                    continue;
                }
                if (fn.desc.equals("[Ljava/awt/Rectangle;")) {
                    addHook(new FieldHook("interfaceBounds", fn));
                } else if (fn.desc.equals("[" + desc("GuidanceArrow"))) {
                    addHook(new FieldHook("guidanceArrows", fn));
                } else if (fn.desc.equals(desc("PlayerFacade"))) {
                    addHook(new FieldHook("playerFacade", fn));
                } else if (fn.desc.equals("[" + desc("Player"))) {
                    addHook(new FieldHook("players", fn));
                } else if (fn.desc.equals("[" + desc("ObjectNode"))) {
                    addHook(new FieldHook("npcObjectNodes", fn));
                } else if (fn.desc.equals("Ljava/awt/Canvas;")) {
                    addHook(new FieldHook("canvas", fn));
                } else if (fn.desc.equals(desc("Scene"))) {
                    addHook(new FieldHook("scene", fn));
                } else if (fn.desc.equals("[" + desc("FriendsChatMember"))) {
                    addHook(new FieldHook("friendsChatMembers", fn));
                } else if (fn.desc.equals("[[" + desc("GrandExchangeOffer"))) {
                    addHook(new FieldHook("grandExchangeOffers", fn));
                } else if (fn.desc.equals("[" + desc("OverheadMessage"))) {
                    addHook(new FieldHook("overheadMessages", fn));
                } else if (fn.desc.equals(desc("ExpandableMenuItem"))) {
                    addHook(new FieldHook("expandedMenuItem", fn));
                } else if (fn.desc.equals("[" + desc("Interface"))) {
                    addHook(new FieldHook("interfaces", fn));
                } else if (fn.desc.equals("[" + desc("InterfaceComponent"))) {
                    addHook(new FieldHook("mainComponents", fn));
                } else if (fn.desc.equals("[" + desc("BufferedConnection"))) {
                    addHook(new FieldHook("bufferedConnections", fn));
                } else if (fn.desc.equals(desc("QuestDefinitionLoader"))) {
                    addHook(new FieldHook("questDefinitionLoader", fn));
                } else if (fn.desc.equals("[" + desc("ArchiveResourceProvider"))) {
                    addHook(new FieldHook("archiveResourceProviders", fn));
                } else if (fn.desc.equals("Ljava/awt/Image;") && (fn.access & ACC_PUBLIC) > 0) {
                    addHook(new FieldHook("image", fn));
                } else if (fn.desc.equals(desc("NetResourceWorker"))) {
                    addHook(new FieldHook("netResourceWorker", fn));
                } else if (fn.desc.equals(desc("ItemTableDefinitionLoader"))) {
                    addHook(new FieldHook("itemTableDefinitionLoader", fn));
                } else if (fn.desc.equals("[" + desc("GlobalPlayer"))) {
                    addHook(new FieldHook("globalPlayers", fn));
                } else if (fn.desc.equals(desc("EnumDefinitionLoader"))) {
                    addHook(new FieldHook("enumDefinitionLoader", fn));
                } else if (fn.desc.equals(desc("ClanChat"))) {
                    if (hooks.containsKey("clanChat")) {
                        addHook(new FieldHook("clanChat1", fn));
                    } else {
                        addHook(new FieldHook("clanChat", fn));
                    }
                }
            }

            for (MethodNode mn : cn.methods) {
                if ((mn.access & ACC_STATIC) != 0) {
                    if (mn.desc.matches("\\(ILjava/lang/String;II(I|B|S|)\\)Z")) {
                        addHook(new InvokeHook("setWorld", mn, "(ILjava/lang/String;II)Z"));
                    } else if (mn.desc.matches("^\\(IIIIL[a-z]+;Z([BSI])?\\)V$")) {
                        addHook(new InvokeHook("itemTableUpdated", mn, "(IIIIZ)V"));
                    } else if (mn.desc.startsWith("(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I"
                            + desc("ChatMessageIcon"))) {
                        addHook(new InvokeHook("messageReceived", mn, "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I"
                                + desc("ChatMessageIcon") + ")V"));
                    } else if (mn.desc.startsWith("(" + desc("MenuItem") + "IIZ")) {
                        addHook(new InvokeHook("processAction", mn, "(" + desc("MenuItem") + "IIZ)V"));
                    } else if (mn.desc.startsWith("(Ljava/lang/String;Ljava/lang/String;IIIJIIZZJZ")) {
                        addHook(new InvokeHook("addMenuItem", mn, "(Ljava/lang/String;Ljava/lang/String;IIIJIIZZJZ)V"));
                    } else if (mn.desc.endsWith(")" + desc("BufferedConnection"))) {
                        addHook(new InvokeHook("getActiveConnection", mn, "()" + desc("BufferedConnection")));
                    } else if (mn.desc.endsWith(")" + desc("ScriptExecutionContext"))) {
                        addHook(new InvokeHook("newScriptContext", mn, "()" + desc("ScriptExecutionContext")));
                    } else if (mn.desc.endsWith("V") && mn.desc.startsWith("(Z")) {
                        int defLoaderRefs = 0;
                        for (AbstractInsnNode ain : mn.instructions.toArray()) {
                            if (ain instanceof FieldInsnNode) {
                                FieldInsnNode fin = (FieldInsnNode) ain;
                                if (fin.desc.startsWith("L") && fin.desc.endsWith(";")) {
                                    String type = fin.desc.replaceFirst("L", "").replaceFirst(";", "");
                                    ClassNode c = updater.classnodes.get(type);
                                    if (c != null && c.superName.equals(clazz("DefinitionCacheLoader"))) {
                                        defLoaderRefs++;
                                    }
                                }
                            }
                        }
                        if (defLoaderRefs > 10) {
                            addHook(new InvokeHook("gc", mn, "(Z)V"));
                        }
                    } else if (mn.desc.matches("\\(" + desc("InterfaceComponent") + "(I|S|B|)\\)" + desc("InterfaceComponentDefinition"))) {
                        addHook(new InvokeHook("loadComponentDefinition", mn, "(" + desc("InterfaceComponent") + ")" + desc("InterfaceComponentDefinition")));
                        visitMethod(mn, new ComponentDefinitionNodeTable());
                    } else if (mn.desc.matches("\\(" + desc("InterfaceComponent") + "(I|S|B|)\\)Ljava/lang/String;")) {
                        addHook(new InvokeHook("getComponentUseAction", mn, "(" + desc("InterfaceComponent") + ")Ljava/lang/String;"));
                    } else if (mn.desc.matches("\\(" + desc("ScriptContext") + "I(I|S|B)\\)V")) {
                        addHook(new InvokeHook("scriptInvoked", mn, "(" + desc("ScriptContext") + "I)V"));
                    }
                }
            }
        }
        visitAll(new AngularMap(), new MapScale(), new MapState(), new ConnectionState(),
                new CollectionStores(), new MenuRowCount(), new RefreshRate(), new Worlds(),
                new LoadingMapAreaConst());
        visitMethodIf(new ProcessingDialog(), m -> m.desc.startsWith("(" + desc("MenuItem") + "IIZ"));
        visitMethodIf(new MenuBounds(), m -> (m.access & ACC_STATIC) != 0 && m.desc.startsWith("(" + desc("RenderConfiguration")));
        visitMethodIf(new Player(), m -> m.desc.length() < 7);
        visitMethodIf(new ConsoleDebug(), m -> m.desc.startsWith("(Ljava/lang/String;ZZ"));
        visitMethodIf(new MenuOpen(), m -> m.desc.startsWith("(" + desc("InterfaceComponent") + "II") && m.desc.endsWith("V")
                && (m.access & ACC_STATIC) != 0);
        visitMethodIf(new ItemDefLoader(), m -> m.desc.startsWith("(" + desc("RenderConfiguration")));
        visitMethodIf(new Credentials1(), m -> m.desc.endsWith(desc("Buffer")));
        PotentialUsername potentialUsername = new PotentialUsername();
        visitAll(potentialUsername);
        visitAll(new Credentials2(potentialUsername));
        visit("Scene", new Cutscene());
        visit("Mobile", new ValidInterfaces());
        visitMethodIf(new BoundsIndex(), m -> m.desc.startsWith("(" + desc("InterfaceComponent")) && m.desc.endsWith("V") && (m.access & ACC_STATIC) != 0);
        visitMethodIf(new LoginState(), m -> m.desc.endsWith("V") && m.parameters() < 2 && findLdc(m, "zap"));
        visitLocalMethodIf(new Loop(), m -> m.desc.endsWith("V") && m.desc.length() <= 4);
        visitMethodIf(new ComponentCycle(), m -> m.desc.startsWith("([" + desc("InterfaceComponent") + "IIIII"));
        //visitAll(new FirstGetfieldMultiplier(updater.visitor("InterfaceComponent").getFieldHook("renderCycle")));
        visitMethodIf(new ObjectUid(), m -> m.desc.startsWith("(" + desc("SceneObject") + "II") && m.desc.endsWith("J") && (m.access & ACC_STATIC) > 0);
        visitMethodIf(new FireScriptOnMob(), mn -> (mn.access & ACC_STATIC) > 0 && mn.desc.contains(";II" + desc("Mobile") + "I"));
        visitMethodIf(new ProcessConsoleCommands(), m -> m.desc.startsWith("(Ljava/lang/String;ZZ"));
        visitMethodIf(new PrintToConsole(), m -> m.desc.endsWith("V") && m.desc.startsWith("(Ljava/lang/String;"));
        visit(new NetResourceWorker());
        visitMethodIf(new InterfaceDefinitionLoader(), m -> m.desc.matches("\\(" + desc("InterfaceComponent") + "II(I|S|B|)\\)V"));
        visitMethodIf(new WidgetSelected(), m -> m.desc.matches("\\(" + desc("InterfaceComponent") + "II(I|S|B|)\\)V"));
        visitMethodIf(new SelectedWidgetActionAndName(), m -> m.desc.matches("\\(" + desc("InterfaceComponent") + "II(I|S|B|)\\)V"));
        visitMethodIf(new SelectedWidget(), m -> m.desc.matches("\\(" + desc("InterfaceComponent") + "II(I|S|B|)\\)V"));
        visitMethodIf(new SelectedWidgetAttribute(), m -> m.desc.matches("\\(" + desc("Interface") + "\\[" + desc("InterfaceComponent") + "IIIIIIII(I|S|B|)\\)V"));
        visitMethodIf(new PlayerActions(), e -> e.desc.startsWith("(" + desc("Player") + "Z") && e.desc.endsWith(")V"));
        visitMethodIf(new ShouldRender(), e -> e.desc.matches("\\(" + desc("InterfaceComponent") + "(I|B|S|)\\)Z"));
        visitMethodIf(new Destination(), m -> m.key().equals(getHookKey("processAction")));
        visitMethodIf(new RandomDat(), e -> e.desc.matches("\\(" + desc("Buffer") + "(I|B|S|)\\)V"));
        visitMethodIf(new ConstructDialogPacket(), e -> Modifier.isStatic(e.access) && e.desc.matches("\\(II(I|B|S|)\\)V"));
        visitMethodIf(new SetConnectionState(), e -> Modifier.isStatic(e.access) && e.desc.matches("\\(I(I|B|S|)\\)V"));
        visitMethodIf(new HopConnectionStateConstant(), e -> e.desc.endsWith(")Z"));
        visitLocalMethodIf(new RenderTick(), e -> e.desc.matches("\\((B|S|I|)\\)V"));
        visitMethodIf(new LoggedInConst(), e -> e.owner.name.equals(clazz("Scene")) && e.desc.endsWith(")Z"));
        visitMethodIf(new LoggedOutConsts(), e -> findLdc(e, "sessionexpired"));
    }

    private static boolean findLdc(MethodNode mn, Object cst) {
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain instanceof LdcInsnNode && ((LdcInsnNode) ain).cst.equals(cst)) {
                return true;
            }
        }
        return false;
    }

    private class BoundsIndex extends BlockVisitor {

        private final String key = getHookKey("validInterfaces");

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.key().equals(key) && fmn.hasParent()) {
                        FieldMemberNode index = (FieldMemberNode) fmn.parent().layer(IMUL, GETFIELD);
                        if (index != null && index.owner().equals(clazz("InterfaceComponent"))) {
                            updater.visitor("InterfaceComponent").addHook(new FieldHook("boundsIndex", index));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class ValidInterfaces extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (!block.owner.desc.startsWith("(IIIIIII")) {
                return;
            }

            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == GETSTATIC && fmn.desc().equals("[Z")) {
                        addHook(new FieldHook("validInterfaces", fmn));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class LoginState extends BlockVisitor {

        private final Multiset<String> write = new Multiset<>();
        private final Multiset<String> read = new Multiset<>();

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals("I")) {
                        if (fmn.opcode() == PUTSTATIC) {
                            write.add(fmn.key());
                        } else if (fmn.opcode() == GETSTATIC) {
                            read.add(fmn.key());
                        }
                    }
                }

                @Override
                public void visitJump(JumpNode jn) {
                    NumberNode value = jn.firstNumber();
                    FieldMemberNode field = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                    if (value != null && field != null) {
                        if (value.number() == 52) {
                            addHook(new FieldHook("loginResponse", field));
                        } else if (value.number() == 42) {
                            addHook(new FieldHook("lobbyResponse", field));
                        }
                    }
                }
            });
        }

        @Override
        public void visitEnd() {
            for (String key : write) {
                int set = write.population(key);
                int get = read.population(key);
                if (set > 35 && get > 20) {
                    String[] val = key.split("\\.");
                    addHook(new FieldHook("loginState", new FieldInsnNode(PUTSTATIC, val[0], val[1], "I")));
                    break;
                }
            }
        }
    }

    private class LoggedOutConsts extends BlockVisitor {

        private final String state = getHookKey("connectionState");
        private final Map<MethodNode, Set<Integer>> states = new HashMap<>();

        @Override
        public void visitEnd() {
            Queue<Map.Entry<MethodNode, Set<Integer>>> pending = new LinkedList<>(states.entrySet());
            for (Map.Entry<MethodNode, Set<Integer>> entry : states.entrySet()) {
                MethodNode mn = entry.getKey();
                if ((mn.access & ACC_PUBLIC) == 0) {
                    addHook(new ConstantHook("CONNECTION_STATE_LOADING_COUNT", "Client", entry.getValue().size()));
                    hook("CONNECTION_STATE_LOADING_", new ArrayList<>(entry.getValue()));
                    pending.remove(entry);
                    break;
                }
            }

            Map.Entry<MethodNode, Set<Integer>> a = pending.poll();
            Map.Entry<MethodNode, Set<Integer>> b = pending.poll();
            if (a == null || b == null || pending.size() > 0) {
                System.err.println("Logged out consts maybe broken?");
                return;
            }

            List<Integer> lobby;
            List<Integer> loggedout;
            if (a.getValue().size() > b.getValue().size()) {
                addHook(new ConstantHook("CONNECTION_STATE_LOGGED_OUT_COUNT", "Client", a.getValue().size()));
                loggedout = new ArrayList<>(a.getValue());

                addHook(new ConstantHook("CONNECTION_STATE_LOBBY_COUNT", "Client", b.getValue().size()));
                lobby = new ArrayList<>(b.getValue());
            } else {
                addHook(new ConstantHook("CONNECTION_STATE_LOGGED_OUT_COUNT", "Client", b.getValue().size()));
                loggedout = new ArrayList<>(b.getValue());

                addHook(new ConstantHook("CONNECTION_STATE_LOBBY_COUNT", "Client", a.getValue().size()));
                lobby = new ArrayList<>(a.getValue());
            }

            hook("CONNECTION_STATE_LOBBY_", lobby);
            hook("CONNECTION_STATE_LOGGED_OUT_", loggedout);
        }

        private void hook(String prefix, List<Integer> values) {
            for (int i = 0; i < values.size(); i++) {
                addHook(new ConstantHook(prefix + i, "Client", values.get(i)));
            }
        }

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.children() <= 2) {
                        FieldMemberNode tgt = (FieldMemberNode) mmn.layer(IMUL, GETSTATIC);
                        if (tgt != null && tgt.key().equals(state)) {
                            MethodNode mn = updater.getMethod(mmn.owner(), mmn.name(), mmn.desc());
                            if (mn == null) {
                                return;
                            }

                            //non public method is loadingStates, the other 2 are lobby and logged out
                            for (Block block : updater.getGraph(mn)) {
                                block.tree().accept(new NodeVisitor() {
                                    @Override
                                    public void visitNumber(NumberNode nn) {
                                        if (!states.containsKey(mn)) {
                                            states.put(mn, new HashSet<>());
                                        }
                                        states.get(mn).add(nn.number());
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    private class LoadingMapAreaConst extends BlockVisitor {

        private final String key = updater.visitor("Scene").getInvokeHook("updateMapArea").key();

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (key.equals(mmn.key())) {
                        AbstractNode node = mmn.child(3);
                        if (node != null && node.opcode() == BIPUSH) {
                            addHook(new ConstantHook("CONNECTION_STATE_LOADING_MAP", "Client",
                                    ((NumberNode) node).number()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class LoggedInConst extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.key().equals(getInvokeHook("setConnectionState").key())) {
                        if (mmn.nextJump() != null) {
                            List<NumberNode> results = mmn.layerAll(NumberNode.class);
                            if (results == null) {
                                return;
                            }

                            NumberNode nn = results.get(0);
                            if (nn != null) {
                                addHook(new ConstantHook("CONNECTION_STATE_LOGGED_IN", "Client", nn.number()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class Worlds extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("[" + desc("World"))) {
                        AbstractNode verify = fmn.layer(IADD, ICONST_1);
                        if (verify != null) {
                            addHook(new FieldHook("worlds", fmn));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class HopConnectionStateConstant extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().matches("\\(ILjava/lang/String;II(I|B|S|)\\)Z")) {
                        AbstractNode an = mmn.parent().next(INVOKESTATIC);
                        if (an != null) {
                            List<NumberNode> results = an.layerAll(NumberNode.class);
                            if (results == null) {
                                return;
                            }
                            addHook(new ConstantHook("CONNECTION_STATE_WORLD_HOPPING", "Client", results.get(0).number()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class SetConnectionState extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    FieldHook hook = getFieldHook("connectionState");
                    if (fmn.putting() && fmn.key().equals(hook.key())) {
                        addHook(new InvokeHook("setConnectionState", block.owner, "(I)V"));
                    }
                }
            });
        }
    }

    private class ConstructDialogPacket extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {

                int count;

                @Override
                public void visitMethod(MethodMemberNode fmn) {
                    if (fmn.owner().equals(clazz("FrameBuffer"))) {
                        count++;
                    }
                }

                public void visitEnd() {
                    if (count == 2) {
                        addHook(new InvokeHook("constructDialogPacket", block.owner, "(II)V"));
                    }
                }
            });
        }
    }


    private class RenderTick extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 1000000L) {
                        addHook(new InvokeHook("onRenderTick", block.owner, "()V"));
                    }
                }
            });
        }
    }

    private class RandomDat extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 24 && nn.preLayer(NEWARRAY) != null) {
                        addHook(new InvokeHook("getRandomDat", block.owner, "(" + desc("Buffer") + ")V"));
                    }
                }
            });
        }
    }

    private class ShouldRender extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    FieldHook fh = updater.visitor("InterfaceComponent").getFieldHook("explicitlyHidden");
                    if (fh != null && fh.key().equals(fmn.key())) {
                        addHook(new InvokeHook("shouldRenderComponent", block.owner, "(" + desc("InterfaceComponent") + ")Z"));
                    }
                }
            });
        }
    }

    private class Destination extends BlockVisitor {

        private final FieldHook secondaryArg;

        private Destination() {
            secondaryArg = updater.visitor("MenuItem").getFieldHook("arg1");
        }

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ISTORE) {
                        FieldMemberNode target = (FieldMemberNode) vn.layer(IMUL, GETFIELD);
                        if (target != null && secondaryArg.key().equals(target.key())) {
                            Client.this.visitMethod(vn.method(), new IdentifySetMethod(vn.var()));
                        }
                    }
                }
            });
        }

        private class IdentifySetMethod extends BlockVisitor {

            private final int baseVar;

            private IdentifySetMethod(int baseVar) {
                this.baseVar = baseVar;
            }

            @Override
            public boolean validate() {
                return !Destination.this.lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitMethod(MethodMemberNode mn) {
                        if (mn.opcode() == INVOKESTATIC && mn.desc().startsWith("(II") && mn.desc().endsWith("V")) {
                            AbstractNode arg0 = mn.child(0);
                            AbstractNode arg1 = mn.child(1);
                            if (arg0 instanceof VariableNode && arg1 instanceof VariableNode) {
                                if (((VariableNode) arg0).var() == baseVar && ((VariableNode) arg1).var() == baseVar + 1) {
                                    Client.this.visitMethod(new IdentifyHooks(), mn.owner(), mn.name(), mn.desc());
                                }
                            }
                        }
                    }
                });
            }
        }

        private class IdentifyHooks extends BlockVisitor {

            @Override
            public boolean validate() {
                return !Destination.this.lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.desc().equals("I") && fmn.opcode() == PUTSTATIC) {
                            VariableNode local = (VariableNode) fmn.layer(IMUL, ILOAD);
                            if (hooks.containsKey("destinationX") && hooks.containsKey("destinationY")) {
                                Destination.this.lock.set(true);
                            } else if (local.var() == 0) {
                                addHook(new FieldHook("destinationX", fmn));
                            } else if (local.var() == 1) {
                                addHook(new FieldHook("destinationY", fmn));
                            }
                        }
                    }
                });
            }
        }
    }

    public class PlayerActions extends BlockVisitor {

        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.getting() && fmn.opcode() == Opcodes.GETSTATIC) {
                        if (fmn.desc().equals("[Ljava/lang/String;")) {
                            addHook(new FieldHook("playerActions", fmn.fin()));
                        } else if (fmn.desc().equals("[S")) {
                            addHook(new FieldHook("playerActionsIndexOffset", fmn.fin()));
                        }
                    }
                }
            });
        }
    }

    public class ComponentDefinitionNodeTable extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == GETSTATIC) {
                        addHook(new FieldHook("componentDefinitionNodeTable", fmn.fin()));
                    }
                }
            });
        }
    }

    public class SelectedWidget extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode nn) {
                    if (nn.opcode() == IF_ICMPEQ || nn.opcode() == IF_ICMPNE) {
                        FieldMemberNode fmn = (FieldMemberNode) nn.layer(IMUL, GETSTATIC);
                        if (fmn != null && nn.layer(ICONST_M1) != null) {
                            addHook(new FieldHook("selectedComponentUid", fmn.fin()));
                        }
                    }
                }
            });
        }
    }

    public class SelectedWidgetActionAndName extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().startsWith("(Ljava/lang/String;Ljava/lang/String;IIIJIIZZJZ")) {
                        AbstractNode node = mmn.layer(INVOKEVIRTUAL, INVOKEVIRTUAL, INVOKEVIRTUAL, INVOKEVIRTUAL, INVOKEVIRTUAL, INVOKEVIRTUAL);
                        if (node != null) {
                            FieldMemberNode action = (FieldMemberNode) node.layer(INVOKESPECIAL, DUP, GETSTATIC);
                            FieldMemberNode name = (FieldMemberNode) node.layer(GETSTATIC);

                            if (action != null && name != null) {
                                addHook(new FieldHook("selectedComponentAction", action.fin()));
                                addHook(new FieldHook("selectedComponentName", name.fin()));
                            }

                        }
                    }
                }
            });
        }
    }

    public class SelectedWidgetAttribute extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 64 && nn.previous(IMUL) != null) {
                        FieldMemberNode fmn = (FieldMemberNode) nn.previous(IMUL).layer(GETSTATIC);
                        if (fmn != null) {
                            addHook(new FieldHook("selectedComponentAttribute", fmn.fin()));
                        }
                    }
                }
            });
        }
    }

    private class WidgetSelected extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals("Z") && fmn.opcode() == Opcodes.PUTSTATIC) {
                        if (fmn.layer(Opcodes.ICONST_1) != null) {
                            addHook(new FieldHook("componentSelected", fmn.fin()));
                        }
                    }
                }
            });
        }
    }

    private class InterfaceDefinitionLoader extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitType(TypeNode tn) {
                    if (tn.opcode() == CHECKCAST && tn.type().equals(clazz("ParameterDefinition"))) {
                        updater.graphs().get(tn.method().owner).get(tn.method())
                                .forEach(graph -> graph.tree().accept(new NodeVisitor() {
                                    @Override
                                    public void visitField(FieldMemberNode fmn) {
                                        ClassNode node = updater.classnodes.get(fmn.desc().replace("L", "").replace(";", ""));
                                        if (node != null && node.superName.equals(clazz("DefinitionCacheLoader"))) {
                                            addHook(new FieldHook("parameterDefinitionLoader", fmn));
                                        }
                                    }
                                }));
                    }
                }
            });
        }
    }

    private class RefreshRate extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 1000000000) {
                        addHook(new FieldHook("refreshRate", (FieldInsnNode) block.get(GETSTATIC)));

                    }
                }
            });
        }
    }

    private class ProcessingDialog extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().endsWith(desc("InterfaceComponent")) && mmn.desc().startsWith("(II")) {
                        if (mmn.parent().opcode() == PUTSTATIC) {
                            FieldMemberNode fmn = (FieldMemberNode) mmn.parent();
                            if (!fmn.desc().equals(desc("InterfaceComponent"))) {
                                return;
                            }
                            addHook(new FieldHook("dialogProcessingComponent", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class NetResourceWorker extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(clazz("NetResourceWorker")) && fmn.hasParent()) {
                        if (fmn.parent().parent() instanceof JumpNode) {
                            NumberNode comparison = fmn.parent().parent().firstNumber();
                            if (comparison != null) {
                                if (comparison.number() == 4) {
                                    updater.visitor("NetResourceWorker").addHook(new FieldHook("errors", fmn.fin()));
                                } else if (comparison.number() == 48) {
                                    updater.visitor("NetResourceWorker").addHook(new FieldHook("status", fmn.fin()));
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class PrintToConsole extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.name().equals("setTime")) {
                        addHook(new InvokeHook("printToConsole", mmn.method(), "(Ljava/lang/String;)V"));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class ProcessConsoleCommands extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 2 && nn.hasParent() && nn.parent() instanceof JumpNode) {
                        FieldMemberNode rights = (FieldMemberNode) nn.parent().layer(IMUL, GETSTATIC);
                        if (rights != null) {
                            addHook(new FieldHook("playerRights", rights.fin()));
                            addHook(new InvokeHook("processConsoleCommand", rights.method(), "(Ljava/lang/String;ZZ)V"));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class FireScriptOnMob extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.owner().equals(clazz("ScriptExecutionContext"))) {
                        updater.visitor("ScriptExecutionContext").addHook(new FieldHook("targetMobileIndex", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class Credentials2 extends BlockVisitor {

        private final PotentialUsername potentialUsername;

        private Credentials2(PotentialUsername potentialUsername) {
            this.potentialUsername = potentialUsername;
        }

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    for (String find : potentialUsername.finds) {
                        if (fmn.key().equalsIgnoreCase(find) && fmn.opcode() == GETSTATIC && fmn.hasParent() && fmn.parent().opcode() == INVOKESTATIC) {
                            MethodMemberNode mmn = (MethodMemberNode) fmn.parent();
                            if (Type.getArgumentTypes(mmn.desc()).length <= 3 && mmn.desc().contains("String;Ljava/lang/String;")) {
                                if (fmn.hasNext() && fmn.next().opcode() == GETSTATIC) {
                                    addHook(new FieldHook("username", fmn.fin()));
                                    addHook(new FieldHook("password", fmn.nextField().fin()));
                                    lock.set(true);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class PotentialUsername extends BlockVisitor {

        private final Set<String> finds;

        private PotentialUsername() {
            finds = new HashSet<>();
        }

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.key().equalsIgnoreCase(((InvokeHook) hooks.get("createCredentialsBuffer")).key())) {
                        updater.graphs().get(block.owner.owner).get(block.owner).forEach(block -> block.tree().accept(new NodeVisitor() {
                            @Override
                            public void visitField(FieldMemberNode fmn) {
                                if (fmn.desc().equals("Ljava/lang/String;") && fmn.hasParent() && fmn.parent().opcode() == INVOKEVIRTUAL) {
                                    MethodMemberNode mmn = (MethodMemberNode) fmn.parent();
                                    if (Type.getArgumentTypes(mmn.desc()).length <= 2 && mmn.desc().endsWith("V")) {
                                        finds.add(fmn.key());
                                    }
                                }
                            }
                        }));
                    }
                }
            });
        }
    }

    private class Credentials1 extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().startsWith("(Ljava/math/BigInteger;Ljava/math/BigInteger;")) {
                        FieldMemberNode fmn = mmn.firstField();
                        if (fmn != null && fmn.hasNext() && fmn.next() instanceof FieldMemberNode) {
                            addHook(new FieldHook("loginRsaExponent", fmn.fin()));
                            addHook(new FieldHook("loginRsaModulus", fmn.nextField().fin()));
                            addHook(new InvokeHook("createCredentialsBuffer", block.owner, "()" + desc("Buffer")));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class ObjectUid extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 0x40000000 && nn.parent().opcode() == IOR) {
                        addHook(new InvokeHook("getObjectUid", nn.method(), "(" + desc("SceneObject") + "II)J"));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class Loop extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IREM) {
                        NumberNode nn = an.firstNumber();
                        FieldMemberNode fmn = an.firstField();
                        if (fmn == null || nn == null || !fmn.isStatic() || !fmn.getting() || nn.number() != 1000) {
                            return;
                        }
                        MethodNode loop = fmn.method();
                        addHook(new FieldHook("engineCycle", fmn.fin(), false));
                        addHook(new InvokeHook("loop", loop, "()V"));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class ComponentCycle extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(clazz("InterfaceComponent"))
                            && fmn.opcode() == PUTFIELD
                            && fmn.desc().equals("I")) {
                        FieldMemberNode setTo = (FieldMemberNode) fmn.layer(IMUL, GETSTATIC);
                        if (setTo != null && setTo.key().equals(getHookKey("engineCycle"))) {
                            updater.visitor("InterfaceComponent").addHook(new FieldHook("renderCycle", fmn.fin()));
                        }
                    }
                }
            });
        }
    }

    private class ItemDefLoader extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    FieldMemberNode gid = (FieldMemberNode) mmn.layer(IMUL, GETFIELD);
                    if (gid != null && gid.owner().equals(updater.visitor("GroundItem").cn.name)) {
                        FieldMemberNode parent = (FieldMemberNode) mmn.layer(GETSTATIC);
                        if (parent != null) {
                            String type = parent.desc().replace("L", "").replace(";", "");
                            ClassNode cn = updater.classnodes.get(type);
                            if (cn != null && cn.superName.equals(clazz("DefinitionCacheLoader"))) {
                                addHook(new FieldHook("itemDefinitionLoader", parent.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class Cutscene extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            if (block.owner.desc.startsWith("(IIIZ") && block.owner.desc.endsWith("V")) {
                block.follow().tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("I")) {
                            if (!hooks.containsKey("cutsceneState")) {
                                addHook(new FieldHook("cutsceneState", fmn.fin()));
                                added++;
                            } else if (!hooks.containsKey("cutsceneId")) {
                                addHook(new FieldHook("cutsceneId", fmn.fin()));
                                added++;
                            }
                        }
                    }
                });
            }
        }
    }

    private class MenuOpen extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.follow().tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IFEQ && jn.hasPrevious() && jn.previous() instanceof JumpNode) {
                        FieldMemberNode fmn = jn.firstField();
                        if (fmn != null && fmn.desc().equals("Z")) {
                            addHook(new FieldHook("menuOpen", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class MenuRowCount extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 21 && nn.hasParent() && nn.parent().opcode() == IADD) {
                        FieldMemberNode rowCount = (FieldMemberNode) nn.parent().layer(IMUL, IMUL, GETSTATIC);
                        if (rowCount == null) {
                            return;
                        }
                        addHook(new FieldHook("menuRowCount", rowCount.fin()));
                    }
                }
            });
        }
    }

    private class CollectionStores extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 8;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitType(TypeNode tn) {
                    if (tn.opcode() == CHECKCAST) {
                        AbstractNode store = tn.parent();
                        if (store != null) {
                            FieldMemberNode collection = (FieldMemberNode) store.layer(INVOKEVIRTUAL, GETSTATIC);
                            if (collection != null) {
                                if (collection.desc().equals(desc("NodeDeque"))) {
                                    if (tn.type().equals(clazz("ProjectileNode")) && !hooks.containsKey("projectileNodeDeque")) {
                                        addHook(new FieldHook("projectileNodeDeque", collection.fin()));
                                        added++;
                                    } else if (tn.type().equals(clazz("MenuItem")) && !hooks.containsKey("menuItemDeque")) {
                                        addHook(new FieldHook("menuItemDeque", collection.fin()));
                                        added++;
                                    }
                                } else if (collection.desc().equals(desc("NodeTable"))) {
                                    if (tn.type().equals(clazz("ExpandableMenuItem")) && !hooks.containsKey("expandableMenuItems")) {
                                        addHook(new FieldHook("expandableMenuItems", collection.fin()));
                                        added++;
                                    } else if (tn.type().equals(clazz("ItemTable")) && !hooks.containsKey("itemTables")) {
                                        addHook(new FieldHook("itemTables", collection.fin()));
                                        added++;
                                    } else if (tn.type().equals(clazz("GroundItemDeque")) && !hooks.containsKey("groundItemDeques")) {
                                        addHook(new FieldHook("groundItemDeques", collection.fin()));
                                        added++;
                                    } else if (tn.type().equals(clazz("AnimableObjectNode")) && !hooks.containsKey("animableObjectNodes")) {
                                        addHook(new FieldHook("animableObjectNodes", collection.fin()));
                                        added++;
                                    } else if (tn.type().equals(clazz("ObjectNode")) && !hooks.containsKey("npcObjectNodeTable")) {
                                        addHook(new FieldHook("npcObjectNodeTable", collection.fin()));
                                        added++;
                                    } else if (tn.type().equals(clazz("InterfaceNode")) && !hooks.containsKey("interfaceNodes")) {
                                        addHook(new FieldHook("interfaceNodes", collection.fin()));
                                        added++;
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class ConsoleDebug extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals(desc("CameraImpl")) && !hooks.containsKey("camera")) {
                        addHook(new FieldHook("camera", fmn.fin()));
                        added++;
                    } else if (fmn.desc().equals(desc("RenderConfiguration")) && !hooks.containsKey("activeRenderConfiguration")) {
                        addHook(new FieldHook("activeRenderConfiguration", fmn.fin()));
                        added++;
                    }
                }
            });
        }
    }

    //    private class SetConnectionState extends BlockVisitor {
    //
    //        private final Map<MethodNode, Integer> referencers;
    //
    //        private SetConnectionState() {
    //            this.referencers = new HashMap<>();
    //        }
    //
    //        @Override
    //        public boolean validate() {
    //            return !lock.get();
    //        }
    //
    //        @Override
    //        public void visit(Block block) {
    //            block.tree().accept(new NodeVisitor() {
    //                @Override
    //                public void visitField(FieldMemberNode fmn) {
    //                    if (fmn.opcode() == PUTSTATIC && fmn.key().equals(getHookKey("connectionState"))) {
    //                        if (referencers.containsKey(fmn.method())) {
    //                            referencers.put(fmn.method(), referencers.get(fmn.method()) + 1);
    //                        } else {
    //                            referencers.put(fmn.method(), 1);
    //                        }
    //                    }
    //                }
    //            });
    //        }
    //
    //        @Override
    //        public void visitEnd() {
    //            MethodNode setter = null;
    //            for (Map.Entry<MethodNode, Integer> referencer : referencers.entrySet()) {
    //                int refs = referencer.getValue();
    //                if (refs == 1) {
    //                    setter = referencer.getKey();
    //                }
    //            }
    //            if (setter != null) {
    //                addHook(new InvokeHook("setConnectionState", setter, "(I)V"));
    //            }
    //        }
    //    }

    private class ConnectionState extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.follow().tree().accept(new NodeVisitor() {
                @Override
                public void visitConstant(ConstantNode cn) {
                    if (cn.cst().equals("js5connect_outofdate") && cn.hasParent()) {
                        AbstractNode an = cn.parent().next();
                        if (an instanceof FieldMemberNode) {
                            FieldMemberNode fmn = (FieldMemberNode) an;
                            if (fmn.desc().equals("I")) {
                                addHook(new FieldHook("connectionState", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class Player extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals(desc("Player")) && fmn.isStatic()) {
                        addHook(new FieldHook("player", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class MenuBounds extends BlockVisitor {

        private int hooked = 0;

        @Override
        public boolean validate() {
            return hooked < 2;
        }

        @Override
        public void visit(Block block) {
            boolean expanded = block.count(new InsnQuery(IFNE)) == 0;
            if (hooks.containsKey("expandedMenuX") && expanded || hooks.containsKey("menuX") && !expanded) {
                return;
            }
            block.tree().accept(new NodeVisitor() {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().startsWith("(" + desc("RenderConfiguration") + "IIIIII")) {
                        AbstractNode xmul = mmn.find(IMUL, 0);
                        if (xmul == null) return;
                        FieldMemberNode x = xmul.firstField();
                        if (x == null || x.opcode() != GETSTATIC) return;
                        AbstractNode ymul = mmn.find(IMUL, 1);
                        if (ymul == null) return;
                        FieldMemberNode y = ymul.firstField();
                        if (y == null || y.opcode() != GETSTATIC) return;
                        AbstractNode wmul = mmn.find(IMUL, 2);
                        if (wmul == null) return;
                        FieldMemberNode w = wmul.firstField();
                        if (w == null || w.opcode() != GETSTATIC) return;
                        AbstractNode hmul = mmn.find(IMUL, 3);
                        if (hmul == null) return;
                        FieldMemberNode h = hmul.firstField();
                        if (h == null || h.opcode() != GETSTATIC) return;
                        addHook(new FieldHook(expanded ? "expandedMenuX" : "menuX", x.fin()));
                        addHook(new FieldHook(expanded ? "expandedMenuY" : "menuY", y.fin()));
                        addHook(new FieldHook(expanded ? "expandedMenuWidth" : "menuWidth", w.fin()));
                        addHook(new FieldHook(expanded ? "expandedMenuHeight" : "menuHeight", h.fin()));
                        hooked++;
                    }
                }
            });
        }
    }

    private class MapState extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new MemberQuery(GETFIELD, clazz("InterfaceComponent"), "I")) != 2 ||
                    block.count(new InsnQuery(ISUB)) != 2 || block.count(new InsnQuery(IDIV)) != 2)
                return;
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode jn) {
                    FieldMemberNode state = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                    if (state != null) {
                        addHook(new FieldHook("mapState", state.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class AngularMap extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new NumberQuery(SIPUSH, 0x3FFF)) == 0 || block.count(new MemberQuery(GETSTATIC)) != 2)
                return;
            block.tree().accept(new NodeVisitor() {
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IAND) {
                        FieldMemberNode offset = (FieldMemberNode) an.layer(IADD, IMUL, GETSTATIC);
                        FieldMemberNode angle = (FieldMemberNode) an.layer(IADD, F2I, GETSTATIC);
                        if (angle == null || offset == null)
                            return;
                        addHook(new FieldHook("mapOffset", offset.fin()));
                        addHook(new FieldHook("mapAngle", angle.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class MapScale extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new InsnQuery(ISHR)) != 2 || block.count(new NumberQuery(SIPUSH, 0x0100)) != 2)
                return;
            block.tree().accept(new NodeVisitor() {
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == ISHR) {
                        FieldMemberNode scale = (FieldMemberNode) an.layer(IMUL, IADD, IMUL, GETSTATIC);
                        if (scale != null) {
                            addHook(new FieldHook("mapScale", scale.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}

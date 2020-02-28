/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.Opcodes;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.query.MemberQuery;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.*;
import org.objectweb.casm.tree.*;
import org.rspeer.api.collections.Multiset;

import java.util.List;

/**
 * @author Dogerina
 * @since 28-06-2015
 */
@VisitorInfo(hooks = {"type", "particleProvider", "parent", "itemId", "itemQuantity", "relativeX", "relativeY",
        "materialId", "width", "height", "componentIndex", "actions", "text", "parentUid", "uid", "components",
        "componentsCopy", "borderThickness", "shadowColor", "toolTip", "name", "contentType", "animator",
        "properties", "renderCycle", "explicitlyHidden", "defaultDefinition", "useAction", "foreground",
        "modelId", "boundsIndex", "loadListeners", "mouseEnterListeners", "mouseExitListeners", "mouseHoverListeners"})
public class InterfaceComponent extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount("[Ljava/lang/Object;") > 15;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("particleProvider", cn.getField(null, desc("ParticleProvider"))));
        addHook(new FieldHook("parent", cn.getField(null, "L" + cn.name + ";")));
        addHook(new FieldHook("actions", cn.getField(null, "[Ljava/lang/String;")));
        addHook(new FieldHook("animator", cn.getField(null, desc("Animator"))));
        addHook(new FieldHook("properties", cn.getField(null, desc("NodeTable"))));
        addHook(new FieldHook("defaultDefinition", cn.getField(null, desc("InterfaceComponentDefinition"))));
        visitLocalMethodIf(new Type(), m -> m.desc.startsWith("(" + desc("Buffer")));
        visitLocalMethodIf(new SpriteFormula(), m -> m.desc.startsWith("(L") && m.desc.endsWith(";"));
        visitMethodIf(new Bounds(), m -> m.desc.startsWith("([L" + cn.name + ";IIIII"));
        visitMethodIf(new Item(), m -> m.desc.startsWith("(L" + cn.name + ";" + desc("Interface") + "Z"));
        visitMethodIf(new Text(), m -> m.desc.startsWith("([L" + cn.name + ";IIIIIII"));
        visitMethodIf(new Foreground(), m -> m.desc.startsWith("([L" + cn.name + ";IIIIIII"));
        visitMethodIf(new ModelId(), m -> m.desc.startsWith("([L" + cn.name + ";IIIIIII"));
        visitMethodIf(new ToolTip(), m -> m.desc.startsWith("(L" + cn.name + ";"));
        visitAll(new Components());
        visitAll(new Index(), new ParentUID(), new UID());
        visitMethodIf(new ContentType(), m -> m.desc.startsWith("(L" + cn.name + ";IIZ"));

        GraphVisitor itfVis = updater.visitor("Interface");
        FieldNode itfField = itfVis.cn.getField(null, "Z");
        visitMethodIf(new Hidden(), m -> {
            return m.desc.startsWith("(L" + cn.name + ";"
                    + desc("Interface")
                    + desc("ScriptExecutionContext")
            ) && m.count(new MemberQuery(INVOKESTATIC, null, null, "\\(L" + cn.name + ";(I|S|B|)\\)V")) > 0
                    && m.count(new MemberQuery(GETFIELD, itfVis.cn.name, itfField.name, "Z")) > 0;
        });

        visitMethodIf(new UseAction(), e -> e.desc.matches("\\(L" + cn.name + ";(I|S|B|)\\)Ljava/lang/String;"));

        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0) {
                if (mn.desc.startsWith("(" + desc("Buffer")) && mn.desc.endsWith("[Ljava/lang/Object;")) {
                    hookListeners(new MemberQuery(Opcodes.INVOKEVIRTUAL, mn));
                    break;
                }
            }
        }
    }

    private void hookListeners(MemberQuery query) {
        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.instructions.count(query) >= 10) {
                int count = mn.instructions.count(query);
                for (int i = 0; i < count; i++) {
                    AbstractInsnNode ain = mn.instructions.get(query, i);
                    if (ain == null) {
                        continue;
                    }

                    AbstractInsnNode next = ain.next();
                    if (next == null || next.opcode() != PUTFIELD) {
                        continue;
                    }

                    FieldInsnNode fin = (FieldInsnNode) next;
                    switch (i) {
                        case 0:
                            addHook(new FieldHook("loadListeners", fin));
                            break;
                        case 1:
                            addHook(new FieldHook("mouseEnterListeners", fin));
                            break;
                        case 2:
                            addHook(new FieldHook("mouseExitListeners", fin));
                            break;
                        case 11:
                            addHook(new FieldHook("mouseHoverListeners", fin));
                            break;
                        default:
                            //addHook(new FieldHook("c" + i, fin));
                            break;
                    }
                }
                break;
            }
        }
    }

    private class UseAction extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.getting() && fmn.desc().equals("Ljava/lang/String;")) {
                        addHook(new FieldHook("useAction", fmn.fin()));
                    }
                }
            });
        }
    }

    private class Hidden extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.getting() && fmn.owner().equals(cn.name) && fmn.desc().equals("Z")) {
                        addHook(new FieldHook("explicitlyHidden", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class ContentType extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE || jn.opcode() == IF_ICMPEQ) {
                        FieldMemberNode test = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                        if (test != null) {
                            FieldMemberNode ctype = (FieldMemberNode) jn.layer(IMUL, GETFIELD);
                            if (ctype != null) {
                                addHook(new FieldHook("contentType", ctype.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class ToolTip extends BlockVisitor {

        @Override
        public boolean validate() {
            return !hooks.containsKey("toolTip") || !hooks.containsKey("name");
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().startsWith("(Ljava/lang/String;Ljava/lang/String;IIIJ")) {
                        FieldMemberNode tt = mmn.firstField();
                        if (tt != null && tt.owner().equals(cn.name) && tt.desc().equals("Ljava/lang/String;")) {
                            addHook(new FieldHook(mmn.first().opcode() == ALOAD ? "name" : "toolTip", tt.fin()));
                        }
                    }
                }
            });
        }
    }

    private class Foreground extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 64) {
                        AbstractNode expr = nn.parent();
                        if (expr != null && expr.opcode() == IREM) {
                            FieldMemberNode subject = (FieldMemberNode) expr.layer(IMUL, GETFIELD);
                            if (subject != null && subject.owner().equals(cn.name)) {
                                addHook(new FieldHook("foreground", subject));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class ModelId extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode expr) {
                    if (expr.desc().endsWith(desc("ItemTable"))) {
                        FieldMemberNode param0 = (FieldMemberNode) expr.layer(IMUL, GETFIELD);
                        if (param0 != null && param0.owner().equals(cn.name)) {
                            addHook(new FieldHook("modelId", param0));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Text extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(cn.name) && fmn.desc().equals("Ljava/lang/String;")) {
                        addHook(new FieldHook("text", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class Index extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            FieldHook hook = getFieldHook("components");
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visit(AbstractNode n) {
                    if (n.opcode() == AALOAD) {
                        FieldMemberNode fmn = n.firstField();
                        if (fmn != null && fmn.key().equals(hook.key())) {
                            FieldMemberNode idx = (FieldMemberNode) n.layer(IMUL, GETFIELD);
                            if (idx != null && idx.owner().equals(cn.name)) {
                                addHook(new FieldHook("componentIndex", idx.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class UID extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKESTATIC && mmn.desc().startsWith("([L" + cn.name + ";IIIZ")) {
                        FieldMemberNode uid = (FieldMemberNode) mmn.layer(IMUL, GETFIELD);
                        if (uid != null && uid.owner().equals(cn.name)) {
                            addHook(new FieldHook("uid", uid.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class ParentUID extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKESTATIC && mmn.desc().endsWith("L" + cn.name + ";")) {
                        FieldMemberNode fmn = (FieldMemberNode) mmn.layer(IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name)) {
                            addHook(new FieldHook("parentUid", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Components extends BlockVisitor {

        private final Multiset<FieldMemberNode> usageCounts = new Multiset<>();

        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(cn.name) && fmn.opcode() == GETFIELD && fmn.desc().equals("[L" + cn.name + ";")) {
                        usageCounts.add(fmn);
                    }
                }
            });
        }

        @Override
        public void visitEnd() {
            FieldMemberNode top = usageCounts.top();
            FieldMemberNode bot = usageCounts.bottom();
            if (top != null && bot != null) {
                addHook(new FieldHook("components", top.fin()));
                addHook(new FieldHook("componentsCopy", bot.fin()));
            }
        }
    }

    private class SpriteFormula extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.follow().tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == LSHL) {
                        NumberNode nn = an.firstNumber();
                        FieldMemberNode fmn = (FieldMemberNode) an.layer(I2L, IMUL, GETFIELD);
                        if (nn == null || fmn == null) {
                            return;
                        }
                        if (nn.number() == 36 && !hooks.containsKey("borderThickness")) {
                            addHook(new FieldHook("borderThickness", fmn.fin()));
                            added++;
                        } else if (nn.number() == 40 && !hooks.containsKey("shadowColor")) {
                            addHook(new FieldHook("shadowColor", fmn.fin()));
                            added++;
                        }
                    } else if (an.opcode() == LADD) {
                        FieldMemberNode txture = (FieldMemberNode) an.layer(I2L, IMUL, GETFIELD);
                        if (txture != null && !hooks.containsKey("materialId")) {
                            addHook(new FieldHook("materialId", txture.fin()));
                            added++;
                        }
                    }
                }
            });
        }
    }

    private class Item extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            int startIndex = org.objectweb.casm.Type.getArgumentTypes(block.owner.desc).length;
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (!vn.hasParent()) {
                        return;
                    }
                    AbstractNode ok = vn.opcode() == ALOAD ? vn.parent() : vn.parent().parent();
                    if (!(ok instanceof FieldMemberNode)) {
                        return;
                    }
                    FieldMemberNode fmn = (FieldMemberNode) ok;
                    if (!fmn.desc().equals("I")) {
                        return;
                    }
                    if (vn.var() == startIndex) {
                        addHook(new FieldHook("itemId", fmn.fin()));
                        added++;
                    } else if (vn.var() == startIndex + 1) {
                        addHook(new FieldHook("itemQuantity", fmn.fin()));
                        added++;
                    }
                }
            });
        }
    }

    private class Bounds extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.name().equals("setBounds")) {
                        FieldMemberNode rect = (FieldMemberNode) mmn.layer(AALOAD, GETSTATIC);
                        if (rect != null && rect.desc().endsWith("[Ljava/awt/Rectangle;")) { //just incase...
                            List<AbstractNode> bounds = mmn.layerAll(IADD, IMUL, GETFIELD);
                            if (bounds == null) {
                                return;
                            }
                            List<AbstractNode> tmp = mmn.layerAll(IMUL, GETFIELD);
                            if (tmp == null) {
                                return;
                            }
                            bounds.addAll(tmp);
                            if (bounds.size() != 4) {
                                return;
                            }
                            addHook(new FieldHook("relativeX", ((FieldMemberNode) bounds.get(0)).fin()));
                            addHook(new FieldHook("relativeY", ((FieldMemberNode) bounds.get(1)).fin()));
                            addHook(new FieldHook("width", ((FieldMemberNode) bounds.get(2)).fin()));
                            addHook(new FieldHook("height", ((FieldMemberNode) bounds.get(3)).fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Type extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IAND) {
                        NumberNode nn = an.firstNumber();
                        if (nn != null && nn.number() == 0x7f) {
                            FieldMemberNode type = (FieldMemberNode) an.layer(IMUL, GETFIELD);
                            if (type != null) {
                                addHook(new FieldHook("type", type.fin()));
                            }
                        }
                    }
                }
            });
        }
    }
}

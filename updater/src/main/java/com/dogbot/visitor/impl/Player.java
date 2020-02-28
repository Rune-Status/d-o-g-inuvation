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
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.query.InsnQuery;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.*;
import org.objectweb.casm.tree.ClassNode;

import java.util.*;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"gender", "p2pLevel", "f2pLevel", "wildernessLevel",
        "totalLevel", "name", "appearance",
        "overheadIcons", "overheadIconFlags"})
public class Player extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount("B") == 1 && cn.fieldCount("Ljava/lang/String;") == 3 && cn.fieldCount("[I") == 2;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("gender", cn.getField(null, "B")));
        addHook(new FieldHook("appearance", cn.getField(null, desc("PlayerAppearance"))));

        visitAll(new CombatLevel(), new WildernessLevel(), new TotalLevel());
        visitLocalMethodIf(new RealName(), m -> m.desc.startsWith("(" + desc("Buffer")));
        visitLocalMethodIf(new OverheadIcons(), m -> m.desc.startsWith("(" + desc("Buffer")));
    }

    private class OverheadIcons extends BlockVisitor {

        private final List<LVS> lvstore = new ArrayList<>();

        private class LVS {

            private final FieldMemberNode key;
            private final int value;

            private LVS(FieldMemberNode key, int value) {
                this.key = key;
                this.value = value;
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
                public void visit(AbstractNode n) {
                    if (n.opcode() == IASTORE && !n.hasChild(ICONST_M1)) {
                        FieldMemberNode array = (FieldMemberNode) n.first(GETFIELD);
                        VariableNode index = (VariableNode) n.last(ILOAD);
                        if (array == null || index == null) {
                            return;
                        }
                        lvstore.add(new LVS(array, index.var()));
                    }
                }
            });
        }

        @Override
        public void visitEnd() {
            if (lvstore.size() < 2) {
                return;
            }

            lvstore.sort(Comparator.comparingInt(lvs -> lvs.value));
            addHook(new FieldHook("overheadIconFlags", lvstore.get(0).key));
            addHook(new FieldHook("overheadIcons", lvstore.get(1).key));
        }
    }

    private class RealName extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals("Ljava/lang/String;") && fmn.opcode() == PUTSTATIC) {
                        FieldMemberNode fan = (FieldMemberNode) fmn.layer(GETFIELD);
                        if (fan != null) {
                            addHook(new FieldHook("name", fan.fin()));
                        }
                    }
                }
            });
        }
    }

    private class CombatLevel extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitMethod(MethodMemberNode mmn) {
                    if (!mmn.hasParent())
                        return;
                    AbstractNode parent = mmn.parent();
                    if (mmn.opcode() == INVOKEVIRTUAL && mmn.name().equals("append")) {
                        ConstantNode constant = mmn.firstConstant();
                        if (constant != null && constant.cst().equals("+")) {
                            AbstractNode n = parent.first(ISUB);
                            if (n != null) {
                                AbstractNode pmul = n.find(IMUL, 0);
                                if (pmul == null) {
                                    return;
                                }
                                FieldMemberNode p2p = pmul.firstField();
                                if (p2p == null || !p2p.owner().equals(cn.name)) {
                                    return;
                                }
                                AbstractNode fmul = n.find(IMUL, 1);
                                if (fmul == null) {
                                    return;
                                }
                                FieldMemberNode f2p = fmul.firstField();
                                if (f2p == null || !f2p.owner().equals(cn.name)) {
                                    return;
                                }
                                addHook(new FieldHook("p2pLevel", p2p.fin()));
                                addHook(new FieldHook("f2pLevel", f2p.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class WildernessLevel extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            if (block.count(new InsnQuery(ISTORE)) == 1 && block.count(new InsnQuery(ICONST_1)) == 1) {
                block.tree().accept(new NodeVisitor() {
                    public void visitJump(JumpNode jn) {
                        if (jn.opcode() == IF_ICMPEQ && jn.first(ICONST_M1) != null) {
                            FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name) && fmn.first(GETSTATIC) != null) {
                                addHook(new FieldHook("wildernessLevel", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }
    }

    private class TotalLevel extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        NumberNode nn = jn.firstNumber();
                        if (nn != null && nn.number() == 65535) {
                            FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name)) {
                                addHook(new FieldHook("totalLevel", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}

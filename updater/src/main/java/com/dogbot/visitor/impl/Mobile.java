/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.Opcodes;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.query.NumberQuery;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.*;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"animator", "modelCache", "orientation", "targetIndex", "particleProvider", "combatGaugeStatusList",
        "overheadMessage", "index", "queueSize", "stanceAnimator", "setOverheadMessage",
        "hitsplatTypes", "hitsplatDamages", "hitsplatCycles", "graphics"
})
public class Mobile extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(byte[].class) >= 1 && cn.fieldCount(boolean.class) >= 3
                && cn.abnormalFieldCount() > 3 && cn.fieldCount(desc("Animator")) == 1;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("overheadMessage", cn.getField(null, desc("OverheadMessage"))));
        addHook(new FieldHook("animator", cn.getField(null, desc("Animator"))));
        addHook(new FieldHook("modelCache", cn.getField(null, "[" + desc("Model"))));
        addHook(new FieldHook("particleProvider", cn.getField(null, desc("ParticleProvider"))));
        addHook(new FieldHook("combatGaugeStatusList", cn.getField(null, desc("StatusList"))));
        addHook(new FieldHook("stanceAnimator", cn.getField(null, desc("MobileAnimator"))));
        addHook(new FieldHook("graphics", cn.getField(null, "[" + desc("MobileSpotAnimation"))));
        visitAll(new Orientation(), new TargetIndex());
        visitMethodIf(new Index(), m -> m.desc.startsWith("(" + desc("MenuItem") + "IIZ"));
        visitMethodIf(new HitSplats(), e -> e.desc.contains("(IIIII"));

        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("(Ljava/lang/String;III")
                    && mn.desc.endsWith("V")) {
                addHook(new InvokeHook("setOverheadMessage", mn, "(Ljava/lang/String;III)V"));
            }
        }
    }

    private class HitSplats extends BlockVisitor {

        private String[] hitsplats = new String[]{"hitsplatTypes", "hitsplatDamages", null, null};

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == Opcodes.GETFIELD && fmn.desc().equals("[I") && fmn.owner().equals(cn.name)) {
                        AbstractNode node = fmn.preLayer(Opcodes.IASTORE);
                        if (node != null) {
                            node = node.layer(Opcodes.ILOAD).next();
                            if (node != null) {
                                if (node.opcode() == Opcodes.IADD) {
                                    addHook(new FieldHook("hitsplatCycles", fmn.fin()));
                                } else if (node.opcode() == Opcodes.ILOAD) {
                                    int var = ((VariableNode) node).var() - 1;
                                    String name = hitsplats[var];
                                    if (name != null) {
                                        addHook(new FieldHook(name, fmn.fin()));
                                    }
                                }
                            }
                        }
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
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.isStatic() && fmn.getting() && fmn.desc().equals(desc("Player"))) {
                        if (!fmn.hasParent() || fmn.parent().opcode() != GETFIELD) {
                            return;
                        }
                        FieldMemberNode idx = (FieldMemberNode) fmn.parent();
                        if (idx.desc().equals("I") && idx.owner().equals(clazz("Player"))) {
                            FieldHook fh = new FieldHook("index", idx.fin());
                            fh.clazz = cn.name;
                            addHook(fh);
                        }
                    }
                }
            });
        }
    }

    private class Orientation extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new NumberQuery(SIPUSH, 16383)) == 1) {
                block.tree().accept(new NodeVisitor() {
                    public void visitOperation(ArithmeticNode an) {
                        if (an.opcode() == IAND) {
                            FieldMemberNode fmn = (FieldMemberNode) an.layer(ISUB, IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name)) {
                                addHook(new FieldHook("orientation", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }
    }

    private class TargetIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new NumberQuery(LDC, 32768)) == 1) {
                block.tree().accept(new NodeVisitor() {
                    public void visitJump(JumpNode jn) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name)) {
                            addHook(new FieldHook("targetIndex", fmn.fin()));
                            lock.set(true);
                        }
                    }
                });
            }
        }
    }
}

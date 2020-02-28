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
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.VariableNode;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 09-08-2015
 */
@VisitorInfo(hooks = {"particleProvider", "animator", "id", "startCycle", "endCycle", "initialTargetDistance",
        "sourceIndex", "targetIndex", "initialHeight", "sourceEquipmentSlot"})
public class Projectile extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.extendsFrom(clazz("SceneEntity")) && cn.fieldCount(double.class) == 5 && cn.fieldCount(int.class) > 3;
    }

    @Override
    public void visit() {
        add("particleProvider", cn.getField(null, desc("ParticleProvider")));
        add("animator", cn.getField(null, desc("Animator")));
        visitLocalMethodIf(new Hooks(), m -> m.name.equals("<init>"));
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 8;
        }

        @Override
        public void visit(final Block block) {
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
                    switch (vn.var()) {
                        case 2:
                            addHook(new FieldHook("id", fmn.fin()));
                            added++;
                            break;
                        case 7:
                            addHook(new FieldHook("initialHeight", fmn.fin()));
                            added++;
                            break;
                        case 8:
                            addHook(new FieldHook("startCycle", fmn.fin()));
                            added++;
                            break;
                        case 9:
                            addHook(new FieldHook("endCycle", fmn.fin()));
                            added++;
                            break;
                        case 11:
                            addHook(new FieldHook("initialTargetDistance", fmn.fin()));
                            added++;
                            break;
                        case 12:
                            addHook(new FieldHook("sourceIndex", fmn.fin()));
                            added++;
                            break;
                        case 13:
                            addHook(new FieldHook("targetIndex", fmn.fin()));
                            added++;
                            break;
                        case 16:
                            addHook(new FieldHook("sourceEquipmentSlot", fmn.fin()));
                            added++;
                            break;
                    }
                }
            });
        }
    }
}

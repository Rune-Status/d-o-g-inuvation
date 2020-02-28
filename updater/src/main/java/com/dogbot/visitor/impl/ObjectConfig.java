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
@VisitorInfo(hooks = {"sceneNode", "model", "particleProvider", "definitionLoader", "id", "animator"})
public class ObjectConfig extends GraphVisitor {

    @Override
    public boolean validate(ClassNode c) {
        return c.ownerless() && c.fieldCount("[Z") == 1 && c.fieldCount("Z") == 4 && c.fieldCount("I") == 6;
    }

    @Override
    public void visit() {
        add("sceneNode", cn.getField(null, desc("SceneNode")));
        add("model", cn.getField(null, desc("Model")));
        add("particleProvider", cn.getField(null, desc("ParticleProvider")));
        visitLocalMethodIf(new Hooks(), m -> m.name.equals("<init>"));
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (!vn.hasParent()) {
                        return;
                    }
                    AbstractNode ok = vn.opcode() == ALOAD ? vn.parent() : vn.parent().parent();
                    if (!(ok instanceof FieldMemberNode)) {
                        return;
                    }
                    if (ok.opcode() == PUTFIELD && ((FieldMemberNode) ok).desc().equals("I")) {
                        FieldMemberNode chk = (FieldMemberNode) ok.layer(IMUL, GETFIELD);
                        if (chk != null && chk.owner().equals(clazz("ObjectDefinition"))) {
                            addHook(new FieldHook("id", ((FieldMemberNode) ok).fin()));
                            added++;
                        }
                    }
                    FieldMemberNode fmn = (FieldMemberNode) ok;
                    switch (vn.var()) {
                        case 2:
                            if (fmn.desc().startsWith("L")) {
                                addHook(new FieldHook("definitionLoader", fmn.fin()));
                                added++;
                            }
                            break;
                    }
                }

                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equalsIgnoreCase(desc("Animator")) && !hooks.containsKey("animator")) {
                        addHook(new FieldHook("animator", fmn.fin()));
                        added++;
                    }
                }
            });
        }
    }
}

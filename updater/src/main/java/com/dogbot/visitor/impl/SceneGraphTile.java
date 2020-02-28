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
import org.objectweb.casm.tree.FieldNode;

/**
 * @author Dogerina
 * @since 05-07-2015
 */
@VisitorInfo(hooks = {"floorLevel", "iterableEntities", "groundEntity", "boundary", "boundary2", "boundaryDecor",
        "boundaryDecor2", "tileDecor"})
public class SceneGraphTile extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.ownerless() && cf.fieldCount(short.class) == 4 && cf.fieldCount(byte.class) == 1
                && cf.abnormalFieldCount() > 2;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("groundEntity", cn.getField(null, desc("GroundEntity"))));
        addHook(new FieldHook("iterableEntities", cn.getField(null, desc("IterableSceneEntity"))));
        visitLocalMethodIf(new Level(), m -> m.desc.equals("(I)V") && m.name.equals("<init>"));
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0) {
                if (fn.desc.equals(desc("Boundary"))) {
                    if (hooks.containsKey("boundary")) {
                        addHook(new FieldHook("boundary2", fn));
                    } else {
                        addHook(new FieldHook("boundary", fn));
                    }
                } else if (fn.desc.equals(desc("BoundaryDecor"))) {
                    if (hooks.containsKey("boundaryDecor")) {
                        addHook(new FieldHook("boundaryDecor2", fn));
                    } else {
                        addHook(new FieldHook("boundaryDecor", fn));
                    }
                } else if (fn.desc.equals(desc("TileDecor"))) {
                    addHook(new FieldHook("tileDecor", fn));
                }
            }
        }
    }

    private class Level extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
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
                    FieldMemberNode fmn = (FieldMemberNode) ok;
                    if (vn.var() == 1 && fmn.desc().equals("B")) {
                        addHook(new FieldHook("floorLevel", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }
}

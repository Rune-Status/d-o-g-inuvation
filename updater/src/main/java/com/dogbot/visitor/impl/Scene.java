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
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.*;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

import java.util.HashMap;

/**
 * @author Dogerina
 * @since 05-07-2015
 */
@VisitorInfo(hooks = {"settings", "graph", "offset", "objectDefinitionLoader", "format", "regionIds",
        "regionData", "updateMapArea"})
public class Scene extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.fieldCount(HashMap.class) == 1 && cf.ownerless() && cf.fieldCount(byte[][].class) > 3;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("settings", cn.getField(null, desc("SceneSettings"))));
        visit(new Graph());
        visit(new RegionIds());
        visitLocalMethodIf(new Offset(), m -> m.desc.endsWith(desc("SceneOffset")));
        visitLocalMethodIf(new UpdateMapArea(), e -> e.owner.name.equalsIgnoreCase(clazz("Scene"))
                && e.desc.startsWith("(IIIZ") && e.desc.endsWith("V"));
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0 && fn.desc.startsWith("L")) {
                String type = fn.desc.replace("L", "").replace(";", "");
                ClassNode cn = updater.classnodes.get(type);
                if (cn != null && cn.superName.equals(clazz("DefinitionCacheLoader"))) {
                    add("objectDefinitionLoader", fn);
                }
            }
        }
        add("regionData", cn.getField(null, "[[[I"));
    }

    private class RegionIds extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == ISHL && an.parent() instanceof ArithmeticNode) {
                        ArithmeticNode parent = (ArithmeticNode) an.parent();
                        NumberNode shift = an.firstNumber();
                        if (parent.opcode() == IADD && shift != null && shift.number() == 8) {
                            FieldMemberNode target = parent.parent().firstField();
                            if (target != null && target.desc().equals("[I")) {
                                addHook(new FieldHook("regionIds", target));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class Offset extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visit(AbstractNode n) {
                    if (n.opcode() == ARETURN) {
                        FieldMemberNode returnResult = n.firstField();
                        if (returnResult != null) {
                            addHook(new FieldHook("offset", returnResult.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Graph extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    VariableNode aload = (VariableNode) fmn.layer(GETFIELD, ALOAD);
                    if (aload != null) {
                        FieldMemberNode graph = (FieldMemberNode) aload.parent();
                        ClassNode cf = updater.classnodes.get(graph.desc().replace("L", "").replace(";", ""));
                        if (cf != null && cf.getField(null, "[[[" + desc("SceneGraphTile")) != null) {
                            addHook(new FieldHook("graph", graph.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class UpdateMapArea extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            addHook(new InvokeHook("updateMapArea", block.owner, "(IIIZ)V"));
            lock.set(true);
        }
    }
}

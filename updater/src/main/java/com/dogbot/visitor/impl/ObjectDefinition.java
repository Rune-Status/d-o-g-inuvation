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
import com.dogbot.visitor.constraint.OpcodeParsingVisitor;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.VariableNode;
import org.objectweb.casm.commons.cfg.tree.util.TreeBuilder;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogerina
 * @since 22-08-2015
 */
@VisitorInfo(hooks = {"name", "actions", "id", "varpIndex", "varpBitIndex", "transformIds", "loader", "parameters",
        "impenetrable", "type", "clippingType", "height", "width", "mapFunction", "itemSupport"})
public class ObjectDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.interfaces.contains(clazz("Definition")) && cn.fieldCount(int[][].class) == 1
                && cn.fieldCount(String.class) == 1 && cn.fieldCount(String[].class) == 1 && cn.fieldCount(byte[].class) == 4;
    }

    @Override
    public void visit() {
        add("name", cn.getField(null, "Ljava/lang/String;"));
        add("actions", cn.getField(null, "[Ljava/lang/String;"));
        add("loader", cn.getField(null, desc("DefinitionLoader")));
        add("parameters", cn.getField(null, desc("NodeTable")));
        visitLocalMethodIf(new Variables(), m -> m.desc.endsWith("L" + cn.name + ";"));
        visitLocalMethodIf(new Constructor(), m -> m.name.equals("<init>"));

        Map<Integer, FieldHook> hooks = new HashMap<>();
        hooks.put(74, createRaw("impenetrable", "Z"));
        hooks.put(75, createRaw("itemSupport", "I"));
        hooks.put(19, createRaw("type", "I"));
        hooks.put(17, createRaw("clippingType", "I"));
        hooks.put(15, createRaw("height", "I"));
        hooks.put(14, createRaw("width", "I"));
        hooks.put(107, createRaw("mapFunction", "I"));

        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("(" + desc("Buffer") + "I")) {
                TreeBuilder.build(mn).accept(new OpcodeParsingVisitor(this, hooks));
            }
        }
    }

    private class Variables extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {

                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKEINTERFACE) {
                        FieldMemberNode idx = (FieldMemberNode) mmn.layer(IMUL, GETFIELD);
                        if (idx != null) {
                            if (mmn.desc().endsWith(desc("VarpBit")) && !hooks.containsKey("varpBitIndex")) {
                                addHook(new FieldHook("varpBitIndex", idx.fin()));
                                added++;
                            } else if (mmn.desc().endsWith(desc("Varp")) && !hooks.containsKey("varpIndex")) {
                                addHook(new FieldHook("varpIndex", idx.fin()));
                                added++;
                            }
                        }
                    }
                }

                @Override
                public void visit(AbstractNode an) {
                    if (!hooks.containsKey("transformIds") && an.opcode() == ARRAYLENGTH) {
                        FieldMemberNode expr = an.firstField();
                        if (expr != null && expr.desc().equals("[I")) {
                            addHook(new FieldHook("transformIds", expr.fin()));
                            added++;
                        }
                    }
                }
            });
        }
    }

    private class Constructor extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
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
                        case 1:
                            addHook(new FieldHook("id", fmn.fin()));
                            lock.set(true);
                            break;
                    }
                }
            });
        }
    }
}

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
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.VariableNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"buckets", "size", "get"})
public class NodeTable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.interfaces != null && cn.interfaces.contains("java/lang/Iterable") && cn.fieldCount(desc("Node")) == 2
                && cn.fieldCount(long.class) == 1 && cn.fieldCount(int.class) == 2;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("buckets", cn.getField(null, "[" + desc("Node"))));
        visitLocalMethodIf(new ConstructorHooks(), m -> m.desc.equals("(I)V") && m.name.equals("<init>"));
        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("(J") && mn.desc.endsWith(desc("Node"))) {
                addHook(new InvokeHook("get", mn, "(J)" + desc("Node")));
            }
        }
    }

    private class ConstructorHooks extends BlockVisitor {

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
                    switch (vn.var()) {
                        case 1:
                            addHook(new FieldHook("size", fmn.fin()));
                            lock.set(true);
                            break;
                    }
                }
            });
        }
    }
}

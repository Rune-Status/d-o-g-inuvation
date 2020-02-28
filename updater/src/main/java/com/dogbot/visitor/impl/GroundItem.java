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
 * @since 07-08-2015
 */
@VisitorInfo(hooks = {"id", "quantity"})
public class GroundItem extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 2 && cn.fieldCount(int.class) == 2 && cn.superName.equals(clazz("Node"))
                && cn.constructors().contains("(II)V") && cn.methodCount() == 1
                && cn.fieldAccessCount(ACC_PUBLIC) == 1 && cn.fieldAccessCount(ACC_PRIVATE) == 0;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Hooks(), m -> m.name.equals("<init>"));
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
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
                    if (!fmn.desc().equals("I")) {
                        return;
                    }
                    switch (vn.var()) {
                        case 1:
                            addHook(new FieldHook("id", fmn.fin()));
                            added++;
                            break;
                        case 2:
                            addHook(new FieldHook("quantity", fmn.fin()));
                            added++;
                            break;
                    }
                }
            });
        }
    }
}

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
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"parameters", "id", "frameDurations"})
public class Animation extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("NodeTable")) == 1 && cn.fieldCount("[I") == 6 && cn.fieldCount("Z") == 2;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("parameters", cn.getField(null, desc("NodeTable"))));
        visitLocalMethodIf(new BlockVisitor() {
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
                        if (vn.var() == 1 && fmn.desc().equals("I")) {
                            addHook(new FieldHook("id", fmn.fin()));
                            lock.set(true);
                        }
                    }
                });
            }
        }, m -> m.name.equals("<init>"));
    }
}

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
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"values"})
public class Varps extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldTypeCount() == 2 && cn.fieldCount(desc("NodeTable")) == 1 && cn.fieldCount("[I") == 2;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitMethod(MethodMemberNode mmn) {
                        if (mmn.opcode() == INVOKEVIRTUAL) {
                            FieldMemberNode fmn = (FieldMemberNode) mmn.layer(IALOAD, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name) && fmn.desc().equals("[I")) {
                                addHook(new FieldHook("values", fmn.fin()));
                            }
                        }
                    }
                });
            }
        }, m -> m.desc.startsWith("(L") && m.desc.endsWith("V"));
    }
}

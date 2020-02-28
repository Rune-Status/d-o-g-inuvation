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
import org.objectweb.casm.commons.cfg.tree.node.VariableNode;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 05-07-2015
 */
@VisitorInfo(hooks = {"z", "x", "y"})
public class Vector3f extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.ownerless() && cf.fieldCount("F") == 3 && cf.fieldTypeCount() == 1;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Hooks(), m -> m.name.equals("<init>") && m.desc.equals("(FFF)V"));
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
                @Override
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() != FLOAD || vn.parent() == null || vn.parent().opcode() != PUTFIELD)
                        return;
                    FieldMemberNode fmn = (FieldMemberNode) vn.parent();
                    switch (vn.var()) {
                        case 1:
                            addHook(new FieldHook("x", fmn.fin()));
                            break;
                        case 2:
                            addHook(new FieldHook("z", fmn.fin()));
                            break;
                        case 3:
                            addHook(new FieldHook("y", fmn.fin()));
                            break;
                    }
                }
            });
        }
    }
}

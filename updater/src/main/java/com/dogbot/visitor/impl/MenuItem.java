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
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"actionText", "targetText", "arg0", "arg1", "arg2", "opcode", "type"})
public class MenuItem extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("DoublyNode")) && cn.fieldCount("Ljava/lang/String;") == 3;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Hooks(), m -> m.name.equals("<init>"));
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 7;
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
                            addHook(new FieldHook("actionText", fmn.fin()));
                            added++;
                            break;
                        case 2:
                            addHook(new FieldHook("targetText", fmn.fin()));
                            added++;
                            break;
                        case 3:
                            addHook(new FieldHook("type", fmn.fin()));
                            added++;
                            break;
                        case 4:
                            addHook(new FieldHook("opcode", fmn.fin()));
                            added++;
                            break;

                        case 9: //TODO debug these, might have em swapped
                            addHook(new FieldHook("arg2", fmn.fin()));
                            added++;
                            break;
                        case 6:
                            addHook(new FieldHook("arg0", fmn.fin()));
                            added++;
                            break;
                        case 8:
                            addHook(new FieldHook("arg1", fmn.fin()));
                            added++;
                            break;
                    }
                }
            });
        }
    }
}

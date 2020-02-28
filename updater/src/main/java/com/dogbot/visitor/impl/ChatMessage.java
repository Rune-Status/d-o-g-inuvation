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
@VisitorInfo(hooks = {"content", "source", "type", "icon"})
public class ChatMessage extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(String.class) == 5 && cn.fieldCount(long.class) == 1 && cn.fieldCount(int.class) == 4;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Hooks(), m -> m.name.equals("<init>"));
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 4;
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
                        case 6:
                            addHook(new FieldHook("content", fmn.fin()));
                            added++;
                            break;
                        case 3:
                            addHook(new FieldHook("source", fmn.fin()));
                            added++;
                            break;
                        case 1:
                            addHook(new FieldHook("type", fmn.fin()));
                            added++;
                            break;
                        case 9:
                            if (fmn.desc().equals(desc("ChatMessageIcon"))) {
                                addHook(new FieldHook("icon", fmn.fin()));
                                added++;
                            }
                            break;
                    }
                }
            });
        }
    }
}

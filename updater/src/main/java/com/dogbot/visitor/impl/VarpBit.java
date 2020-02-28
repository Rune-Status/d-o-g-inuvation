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

import java.util.List;

/**
 * @author Dogerina
 * @since 22-08-2015
 */
@VisitorInfo(hooks = {"name", "varp", "id", "start", "end"})
public class VarpBit extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("Varp")) == 1 && cn.fieldCount("Ljava/lang/String;") >= 1 && cn.fieldCount("I") > 2;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("name", cn.getField(null, "Ljava/lang/String;")));
        addHook(new FieldHook("varp", cn.getField(null, desc("Varp"))));
        visitLocalMethodIf(new Constructor(), m -> m.name.equals("<init>"));
        visitLocalMethodIf(new Bits(), m -> m.desc.startsWith("(I") && m.desc.endsWith(")I"));
    }

    private class Bits extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visit(AbstractNode n) {
                    if (n.opcode() == IALOAD) {
                        List<AbstractNode> maskIndex = n.layerAll(ISUB, IMUL, GETFIELD);
                        if (maskIndex != null && maskIndex.size() == 2) {
                            addHook(new FieldHook("start", ((FieldMemberNode) maskIndex.get(0)).fin()));
                            addHook(new FieldHook("end", ((FieldMemberNode) maskIndex.get(1)).fin()));
                            lock.set(true);
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

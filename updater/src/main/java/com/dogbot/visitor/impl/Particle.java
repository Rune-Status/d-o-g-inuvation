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
 * @since 09-08-2015
 */
@VisitorInfo(hooks = {"speed", "configuration"})
public class Particle extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return !cn.ownerless() && cn.fieldCount(short.class) == 7 && cn.fieldCount(int.class) == 2 && cn.abnormalFieldCount() == 1;
    }

    @Override
    public void visit() {
        add("configuration", cn.getField(null, desc("ParticleConfiguration")));
        visitLocalMethodIf(new Constructor(), m -> m.name.equals("<init>"));
    }

    private class Constructor extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 1;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (!vn.hasParent()) {
                        return;
                    }
                    AbstractNode ok = vn.parent();
                    if (ok == null) {
                        return;
                    }
                    if (!(ok instanceof FieldMemberNode)) {
                        ok = ok.parent();
                    }
                    if (!(ok instanceof FieldMemberNode)) {
                        return;
                    }
                    FieldMemberNode fmn = (FieldMemberNode) ok;
                    switch (vn.var()) {
                        case 8:
                            addHook(new FieldHook("speed", fmn.fin()));
                            added++;
                            break;
                    }
                }
            });
        }
    }
}

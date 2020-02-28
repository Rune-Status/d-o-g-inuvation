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
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

/**
 * @author Dogerina
 * @since 09-08-2015
 */
@VisitorInfo(hooks = {"head", "current"})
public class StatusList extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        String node = clazz("StatusNode");
        return !cn.name.equals(node) && cn.ownerless() && cn.fieldTypeCount() == 1 &&
                cn.fieldCount("L" + node + ";") == 2;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Head(), m -> m.desc.endsWith("Z"));
        FieldHook fh = getFieldHook("head");
        if (fh != null) {
            for (FieldNode fn : cn.fields) {
                if ((fn.access & ACC_STATIC) == 0 && fn.desc.equals(desc("StatusNode"))) {
                    if (fn.name.equals(fh.field)) {
                        continue;
                    }
                    addHook(new FieldHook("current", fn));
                }
            }
        }
    }

    private class Head extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals(desc("StatusNode")) && fmn.owner().equals(cn.name)) {
                        addHook(new FieldHook("head", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }
}

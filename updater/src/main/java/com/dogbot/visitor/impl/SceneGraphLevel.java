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
import org.objectweb.casm.tree.FieldNode;

import java.lang.reflect.Modifier;

/**
 * @author Dogerina
 * @since 05-07-2015
 * a 'plane' on the scenegraph
 */
@VisitorInfo(hooks = {"tileHeights", "width", "length"})
public class SceneGraphLevel extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.ownerless() && Modifier.isAbstract(cf.access)
                && cf.fieldCount(int.class) == 4 && cf.abnormalFieldCount() == 0;
    }

    @Override
    public void visit() {
        FieldNode fn = cn.getField(null, "[[I");
        if (fn != null) {
            addHook(new FieldHook("tileHeights", fn));
        }
        visitLocalMethodIf(new Size(), m -> m.name.equals("<init>"));
    }

    private class Size extends BlockVisitor {

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
                            addHook(new FieldHook("width", fmn.fin()));
                            added++;
                            break;
                        case 2:
                            addHook(new FieldHook("length", fmn.fin()));
                            added++;
                            break;
                    }
                }
            });
        }
    }
}

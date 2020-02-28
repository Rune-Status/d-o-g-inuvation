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

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"height", "floorLevel"})
public class SceneNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(byte.class) == 2 && cn.fieldCount(int.class) == 1 && (cn.access & ACC_ABSTRACT) != 0;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0) {
                if (fn.desc.equals("I")) {
                    addHook(new FieldHook("height", fn));
                }
            }
        }
        visitLocalMethodIf(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitVariable(VariableNode vn) {
                        if (vn.opcode() == LSTORE) {
                            AbstractNode an = vn.layer(LALOAD, AALOAD, AALOAD, GETFIELD, ALOAD);
                            if (an != null) {
                                FieldMemberNode fmn = (FieldMemberNode) an.parent();
                                addHook(new FieldHook("floorLevel", fmn.fin()));
                            }
                        }
                    }
                });
            }
        }, m -> m.desc.startsWith("(II[L") && m.desc.endsWith("I"));
    }
}

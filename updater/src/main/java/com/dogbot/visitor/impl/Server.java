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
import org.objectweb.casm.commons.cfg.tree.node.ArithmeticNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
@VisitorInfo(hooks = {"mask", "population"})
public class Server extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & ACC_ABSTRACT) != 0 && cn.fieldCount() == 3 && cn.fieldTypeCount() == 1 && cn.fieldAccessCount(ACC_PUBLIC) == 2;
    }

    @Override
    public void visit() {
        visit(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitOperation(ArithmeticNode an) {
                        if (an.opcode() == IAND) {
                            FieldMemberNode fmn = (FieldMemberNode) an.layer(IMUL, GETFIELD);
                            if (fmn != null) {
                                addHook(new FieldHook("mask", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        });
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0 && (fn.access & ACC_PUBLIC) != 0 && !isValueHooked(cn.name, fn.name)) {
                addHook(new FieldHook("population", fn));
            }
        }
    }
}

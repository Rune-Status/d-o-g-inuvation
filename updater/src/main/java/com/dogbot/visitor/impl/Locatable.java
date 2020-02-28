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
@VisitorInfo(hooks = {"positionSpace", "pos"})
public class Locatable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("CoordinateSpace")) == 3 && cn.fieldCount("L" + cn.name + ";") == 3;
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
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.hasParent() && fmn.parent().opcode() == INVOKEVIRTUAL) {
                            if (fmn.hasNext() && fmn.next().opcode() == INVOKEVIRTUAL) {
                                FieldMemberNode testCheck = fmn.next().firstField();
                                if (testCheck != null && fmn.desc().equals(desc("CoordinateSpace"))) {
                                    addHook(new FieldHook("positionSpace", fmn.fin()));
                                }
                            }
                        }
                    }
                });
            }
        }, m -> m.desc.endsWith(desc("CoordinateSpace")));
        visitLocalMethodIf(new Position(), e -> e.desc.startsWith("(" + desc("CoordinateSpace")));
    }

    private class Position extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().contains("(" + desc("CoordinateSpace")) && mmn.layer(GETFIELD) != null) {
                        addHook(new FieldHook("pos", ((FieldMemberNode) mmn.layer(GETFIELD)).fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }
}

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
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"topId", "topQuantity", "middleId", "middleQuantity", "bottomId", "bottomQuantity"})
public class ItemPile extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("GroundEntity")) && cn.fieldCount("I") >= 10;
    }

    @Override
    public void visit() {
        visitAll(new Ids());
        visitAll(new Stacks());
    }

    private class Ids extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.follow().tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ISTORE) {
                        String name = null;
                        if (vn.var() == 11) {
                            name = "topId";
                        } else if (vn.var() == 12) {
                            name = "middleId";
                        } else if (vn.var() == 13) {
                            name = "bottomId";
                        }
                        if (name != null && !hooks.containsKey(name)) {
                            FieldMemberNode fmn = (FieldMemberNode) vn.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name) && !isValueHooked(fmn.owner(), fmn.name())) {
                                addHook(new FieldHook(name, fmn.fin()));
                                added++;
                            }
                        }
                    }
                }
            });
        }
    }

    private class Stacks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ASTORE) {
                        FieldMemberNode id = (FieldMemberNode) vn.layer(INVOKEVIRTUAL, INVOKEVIRTUAL, IMUL, GETFIELD);
                        if (id == null || !id.owner().equals(cn.name)) {
                            return;
                        }
                        FieldMemberNode size = (FieldMemberNode) vn.layer(INVOKEVIRTUAL, IMUL, GETFIELD);
                        if (size == null || !size.owner().equals(cn.name)) {
                            return;
                        }
                        String name = null;
                        if (id.key().equals(getHookKey("topId"))) {
                            name = "topQuantity";
                        } else if (id.key().equals(getHookKey("middleId"))) {
                            name = "middleQuantity";
                        } else if (id.key().equals(getHookKey("bottomId"))) {
                            name = "bottomQuantity";
                        }
                        if (name == null || hooks.containsKey(name) || isValueHooked(size.owner(), size.name())) {
                            return;
                        }
                        addHook(new FieldHook(name, size.fin()));
                        added++;
                    }
                }
            });
        }
    }
}

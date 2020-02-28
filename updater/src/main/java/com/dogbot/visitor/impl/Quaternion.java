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
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"x", "y", "z", "w"})
public class Quaternion extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldTypeCount() == 1 && cn.fieldCount("F") == 4 &&
                cn.constructors().contains("(L" + cn.name + ";)V");
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Hooks(), m -> m.name.equals("<init>") && m.desc.equals("(FFFF)V"));
    }

    private class Hooks extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 4;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.owner().equals(cn.name)) {
                        updater.graphs().get(cn).get(cn.getMethod(mmn.name(), mmn.desc())).forEach(b -> {
                            b.tree().accept(new NodeVisitor() {
                                @Override
                                public void visitField(FieldMemberNode fmn) {
                                    if (!fmn.desc().equals("F")) {
                                        return;
                                    }
                                    if (hooks.get("x") == null) {
                                        addHook(new FieldHook("x", fmn.fin()));
                                        added++;
                                    } else if (hooks.get("y") == null) {
                                        addHook(new FieldHook("y", fmn.fin()));
                                        added++;
                                    } else if (hooks.get("z") == null) {
                                        addHook(new FieldHook("z", fmn.fin()));
                                        added++;
                                    } else if (hooks.get("w ") == null) {
                                        addHook(new FieldHook("w", fmn.fin()));
                                        added++;
                                    }
                                }
                            });
                        });
                    }
                }
            });
        }
    }
}

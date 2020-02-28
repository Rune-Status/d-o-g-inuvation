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
import org.objectweb.casm.commons.cfg.query.NumberQuery;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.*;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"floorLevel", "x", "y", "type", "id", "targetIndex"})
public class GuidanceArrow extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldTypeCount() == 1 && cn.fieldCount("I") == 10 && cn.methodCount((String) null) == 1;
    }

    @Override
    public void visit() {
        visitAll(new Level(), new Position(), new Type(), new Id(), new TargetIndex());
    }

    private class Level extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == LSTORE) {
                        FieldMemberNode fmn = (FieldMemberNode) vn.layer(I2L, ISHL, IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name)) {
                            addHook(new FieldHook("floorLevel", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Position extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            if (block.count(new NumberQuery(SIPUSH, 128)) == 4) {
                block.tree().accept(new NodeVisitor() {
                    public void visitOperation(ArithmeticNode an) {
                        if (an.opcode() == IDIV) {
                            AbstractNode n = an.parent().parent();
                            if (n != null && n.opcode() == ISTORE) {
                                FieldMemberNode fmn = (FieldMemberNode) an.layer(IMUL, GETFIELD);
                                if (fmn != null && fmn.owner().equals(cn.name)) {
                                    if (!hooks.containsKey("x")) {
                                        addHook(new FieldHook("x", fmn.fin()));
                                        added++;
                                    } else if (!hooks.containsKey("y")) {
                                        FieldHook hook = new FieldHook("y", fmn.fin());
                                        hook.multiplier = 1688015233;
                                        addHook(hook);
                                        added++;
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private class Type extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitJump(JumpNode jn) {
                    NumberNode nn = jn.firstNumber();
                    if (nn != null && nn.number() == 6) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETFIELD);
                        if (fmn != null && fmn.owner().equals(cn.name) && fmn.first(ALOAD) != null) {
                            addHook(new FieldHook("type", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Id extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKESTATIC) {
                        NumberNode nn = mmn.firstNumber();
                        if (nn != null && nn.number() == 360000) {
                            FieldMemberNode fmn = (FieldMemberNode) mmn.layer(IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name)) {
                                addHook(new FieldHook("id", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class TargetIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(CHECKCAST) == 1) {
                block.tree().accept(new NodeVisitor() {
                    public void visitVariable(VariableNode vn) {
                        if (vn.opcode() == ASTORE) {
                            FieldMemberNode fmn = (FieldMemberNode) vn.layer(INVOKEVIRTUAL, I2L, IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name)) {
                                addHook(new FieldHook("targetIndex", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }
    }
}

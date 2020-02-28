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
import org.objectweb.casm.Type;
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
@VisitorInfo(hooks = {"entry1x1", "entry1x2", "entry1x3", "entry2x1", "entry2x2", "entry2x3",
        "entry3x1", "entry3x2", "entry3x3", "entry4x1", "entry4x2", "entry4x3"})
public class Matrix4x3 extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldTypeCount() == 1 && cn.fieldCount(float.class) == 12;
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
                    public void visitVariable(VariableNode vn) {
                        if (vn.opcode() != FLOAD || vn.parent() == null || vn.parent().opcode() != PUTFIELD)
                            return;
                        FieldMemberNode fmn = (FieldMemberNode) vn.parent();
                        switch (vn.var()) {
                            case 1:
                                addHook(new FieldHook("entry1x1", fmn.fin()));
                                break;
                            case 2:
                                addHook(new FieldHook("entry1x2", fmn.fin()));
                                break;
                            case 3:
                                addHook(new FieldHook("entry1x3", fmn.fin()));
                                break;
                            case 4:
                                addHook(new FieldHook("entry2x1", fmn.fin()));
                                break;
                            case 5:
                                addHook(new FieldHook("entry2x2", fmn.fin()));
                                break;
                            case 6:
                                addHook(new FieldHook("entry2x3", fmn.fin()));
                                break;
                            case 7:
                                addHook(new FieldHook("entry3x1", fmn.fin()));
                                break;
                            case 8:
                                addHook(new FieldHook("entry3x2", fmn.fin()));
                                break;
                            case 9:
                                addHook(new FieldHook("entry3x3", fmn.fin()));
                                break;
                        }
                    }
                });
            }
        }, m -> m.desc.endsWith("V") && m.desc.startsWith("(FFFFFFFFF"));
        visitLocalMethodIf(new BlockVisitor() {

            private int added = 0;

            @Override
            public boolean validate() {
                return added < 3;
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitVariable(VariableNode vn) {
                        if (vn.opcode() != FLOAD || vn.parent() == null || vn.parent().opcode() != PUTFIELD) {
                            return;
                        }
                        FieldMemberNode fmn = (FieldMemberNode) vn.parent();
                        if (isValueHooked(fmn.owner(), fmn.name())) {
                            return;
                        }
                        switch (vn.var()) {
                            case 1:
                                addHook(new FieldHook("entry4x1", fmn.fin()));
                                added++;
                                break;
                            case 2:
                                addHook(new FieldHook("entry4x2", fmn.fin()));
                                added++;
                                break;
                            case 3:
                                addHook(new FieldHook("entry4x3", fmn.fin()));
                                added++;
                                break;
                        }
                    }
                });
            }
        }, m -> m.desc.startsWith("(FFF") && Type.getArgumentTypes(m.desc).length < 5 && m.desc.endsWith("V"));
    }
}

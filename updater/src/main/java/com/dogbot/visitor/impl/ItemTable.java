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
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 01-08-2015
 */
@VisitorInfo(hooks = {"ids", "quantities"})
public class ItemTable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldTypeCount() == 2 && cn.fieldCount() == 3 && cn.fieldCount("[I") == 2;
    }

    /*
            this.i = new int[]{-1}; //id array is always initialized with -1 in it
            this.l = new int[]{0}; //and quantities always 0
     */
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
                        if (fmn.putting()) {
                            AbstractNode store = fmn.layer(IASTORE);
                            if (store != null && store.children() == 3) {
                                AbstractNode valueToStore = store.child(2);
                                if (valueToStore != null) {
                                    if (valueToStore.opcode() == ICONST_M1) {
                                        addHook(new FieldHook("ids", fmn.fin()));
                                    } else if (valueToStore.opcode() == ICONST_0) {
                                        addHook(new FieldHook("quantities", fmn.fin()));
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }, m -> m.name.equals("<init>"));
    }
}

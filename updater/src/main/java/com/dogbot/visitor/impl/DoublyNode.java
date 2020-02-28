/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.JumpNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"subHash", "subNext", "subPrevious", "unlinkSub"})
public class DoublyNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount("L" + cn.name + ";") == 2
                && cn.fieldCount("J") == 1 && cn.fieldCount() == 3;
    }

    @Override
    public void visit() {
        add("subHash", cn.getField(null, "J"));
        visitLocalMethodIf(new Hooks(), m -> m.desc.endsWith("V"));
    }

    private class Hooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    FieldMemberNode fmn = jn.firstField();
                    if (fmn != null && fmn.desc().equals("L" + cn.name + ";")) {
                        addHook(new FieldHook("subPrevious", fmn.fin()));
                        for (FieldNode fn : cn.fields) {
                            if (fn.desc.equals("L" + cn.name + ";")) {
                                if (!fn.name.equals(fmn.name())) {
                                    addHook(new FieldHook("subNext", fn));
                                    addHook(new InvokeHook("unlinkSub", block.owner, "()V"));
                                    break;
                                }
                            }
                        }
                        lock.set(true);
                    }
                }
            });
        }
    }
}

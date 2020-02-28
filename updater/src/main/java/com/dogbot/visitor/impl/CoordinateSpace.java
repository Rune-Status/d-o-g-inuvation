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
 * @since 05-07-2015
 */
@VisitorInfo(hooks = {"rotation", "translation"})
public class CoordinateSpace extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cf) {
        return cf.fieldCount() == 3 && cf.ownerless() && cf.constructors().contains("(L" + cf.name + ";)V")
                && cf.fieldCount(desc("Quaternion")) == 1 && cf.fieldCount(desc("Vector3f")) == 2;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Rotation(), m -> m.name.equals("toString"));
        visitLocalMethodIf(new Translation(), m -> m.name.equals("<init>") && m.desc.equals("()V"));
    }

    private class Rotation extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals(desc("Quaternion"))) {
                        addHook(new FieldHook("rotation", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class Translation extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD && fmn.desc().equals(desc("Vector3f"))) {
                        MethodMemberNode mmn = (MethodMemberNode) fmn.layer(INVOKESPECIAL);
                        if (mmn != null && mmn.desc().equals("()V")) {
                            addHook(new FieldHook("translation", fmn));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.InvokeHook;
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
 * @since 10-08-2015
 */
@VisitorInfo(hooks = {"getYaw", "getPitch"})
public class Camera extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & ACC_ABSTRACT) > 0 && cn.fieldCount(desc("Vector3f")) == 6;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new BlockVisitor() {

            private int added = 0;

            @Override
            public boolean validate() {
                return added < 2;
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitMethod(MethodMemberNode mmn) {
                        if (mmn.name().equals("atan2")) {
                            FieldMemberNode fmn = (FieldMemberNode) mmn.layer(F2D, GETFIELD);
                            if (fmn != null && fmn.desc().equals("F") && !hooks.containsKey("getYaw")) {
                                addHook(new InvokeHook("getYaw", fmn.method(), "()F"));
                                added++;
                                return;
                            }
                            if ((fmn = (FieldMemberNode) mmn.layer(F2D, FNEG, GETFIELD)) != null
                                    && fmn.desc().equals("F") && !hooks.containsKey("getPitch")) {
                                addHook(new InvokeHook("getPitch", fmn.method(), "()F"));
                                added++;
                            }
                        }
                    }
                });
            }
        }, m -> m.desc.endsWith("F"));
    }
}

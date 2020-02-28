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
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"animation", "animationFrame"})
public class Animator extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("Animation")) == 1 && cn.fieldCount(int.class) > 3;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("animation", cn.getField(null, desc("Animation"))));
        visitLocalMethodIf(new AnimateModel(), m -> m.desc.startsWith("(" + desc("Model") + "I"));
    }

    private class AnimateModel extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            GraphVisitor animation = updater.visitor("Animation");
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (Type.getArgumentTypes(mmn.desc()).length > 6 && mmn.desc().contains("IIZ")) {
                        AbstractNode arrayLoad = mmn.first(IALOAD);
                        if (arrayLoad != null) {
                            FieldMemberNode array = arrayLoad.firstField();
                            FieldMemberNode index = (FieldMemberNode) arrayLoad.layer(IMUL, GETFIELD);
                            if (array != null && array.owner().equals(animation.cn.name)
                                    && array.desc().equals("[I") && index != null) {
                                addHook(new FieldHook("animationFrame", index.fin()));
                                animation.addHook(new FieldHook("frameDurations", array.fin()));
                            }
                        }
                    }
                }
            });
        }
    }
}

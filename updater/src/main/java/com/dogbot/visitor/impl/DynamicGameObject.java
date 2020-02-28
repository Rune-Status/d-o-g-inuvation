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
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.ArithmeticNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"id", "model", "type", "orientation"})
public class DynamicGameObject extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("SceneEntity")) && cn.fieldCount("Z") >= 5;
    }

    @Override
    public void visit() {
        add("model", cn.getField(null, desc("Model")));
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0 && (fn.access & ACC_FINAL) != 0) {
                if (fn.desc.equals("I")) {
                    addHook(new FieldHook("id", fn));
                }
            }
        }

        visitLocalMethodIf(new Orientation(), m -> m.desc.startsWith("(" + desc("RenderConfiguration")));

        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0 && fn.desc.equals("B")) {
                FieldHook fh = getFieldHook("orientation");
                if (fh != null && !fn.name.equals(fh.field)) {
                    addHook(new FieldHook("type", fn));
                }
            }
        }

        visitLocalMethodIf(new Returning("id", "getId"), m -> m.desc.endsWith("I"));
        visitLocalMethodIf(new Returning("type", "getType"), m -> m.desc.endsWith("I"));
        visitLocalMethodIf(new Returning("orientation", "getOrientation"), m -> m.desc.endsWith("I"));
    }

    private class Orientation extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IADD && an.hasChild(ICONST_4)) {
                        FieldMemberNode fmn = an.firstField();
                        if (fmn != null) {
                            addHook(new FieldHook("orientation", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Returning extends BlockVisitor {

        private final String key, invoker;

        private Returning(String key, String invoker) {
            this.key = key;
            this.invoker = invoker;
        }

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visit(AbstractNode an) {
                    if (an.opcode() == IRETURN) {
                        FieldMemberNode fmn = (FieldMemberNode) an.layer(IMUL, GETFIELD);
                        if (fmn == null) {
                            fmn = an.firstField();
                        }
                        if (fmn != null) {
                            FieldHook fh = getFieldHook(key);
                            if (fh != null && fmn.key().equals(fh.key())) {
                                InvokeHook hook = new InvokeHook(invoker, an.method(), "()I");
                                hook.clazz = updater.visitor("SceneObject").cn.name;
                                updater.visitor("SceneObject").addHook(hook); //had to do this to fix injector
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}

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
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.NumberNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

import java.util.List;

/**
 * @author Dogerina
 * @since 23-07-2015
 */
@VisitorInfo(hooks = {"absoluteX", "absoluteY", "multiplierX", "multiplierY", "update", "matrix4f", "nativeHeap", "byteBuffer"})
public class DirectXRenderConfiguration extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("RenderConfiguration")) && (cn.access & ACC_ABSTRACT) != 0;
    }

    @Override
    public void visit() {
        InvokeHook update = RenderConfiguration.findUpdate(this);
        if (update != null) {
            addHook(update);
            FieldHook matrix4f = RenderConfiguration.findMatrix4f(this);
            if (matrix4f != null) {
                addHook(matrix4f);
            }
        }
        List<FieldMemberNode> hooks = RenderConfiguration.findHooks(this);
        if (hooks != null) {
            while (hooks.size() > 4) {
                hooks.remove(0);
            }
            for (FieldMemberNode hook : hooks) {
                NumberNode index = (NumberNode) hook.top().first(ICONST_0);
                if (index == null) {
                    index = (NumberNode) hook.top().first(ICONST_1);
                }
                if (index == null) {
                    continue;
                }
                String hookName = hook.parent().opcode() == FADD ? "absolute" : "multiplier";
                hookName += index.number() == 0 ? "X" : "Y";
                addHook(new FieldHook(hookName, hook.fin()));
            }
        }
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0) {
                if (fn.desc.endsWith("ByteBuffer;")) {
                    addHook(new FieldHook("byteBuffer", fn));
                } else if (fn.desc.endsWith("NativeHeap;")) {
                    addHook(new FieldHook("nativeHeap", fn));
                }
            }
        }
    }
}

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
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

import java.lang.reflect.Modifier;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"tail", "head"})
public class DoublyNodeQueue extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldTypeCount() == 1 && cn.fieldCount(desc("DoublyNode")) == 2;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if (Modifier.isStatic(fn.access)) {
                continue;
            }
            if (Modifier.isPublic(fn.access)) {
                addHook(new FieldHook("tail", fn));
            } else {
                addHook(new FieldHook("head", fn));
            }
        }
    }
}

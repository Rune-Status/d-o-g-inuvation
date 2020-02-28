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

/**
 * @author Dogerina
 * @since 01-08-2015
 */
@VisitorInfo(hooks = {"queue"})
public class DoublyNodeQueueIterator extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldTypeCount() == 2 && cn.fieldCount(desc("DoublyNodeQueue")) == 1
                && cn.fieldCount(desc("DoublyNode")) == 2;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0) {
                if (fn.desc.equals(desc("DoublyNodeQueue"))) {
                    addHook(new FieldHook("queue", fn));
                }
            }
        }
    }
}

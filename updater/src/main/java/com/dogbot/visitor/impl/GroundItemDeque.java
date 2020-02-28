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

/**
 * @author Dogerina
 * @since 10-08-2015
 */
@VisitorInfo(hooks = {"deque"})
public class GroundItemDeque extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldCount(desc("NodeDeque")) == 1;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("deque", cn.getField(null, desc("NodeDeque"))));
    }
}

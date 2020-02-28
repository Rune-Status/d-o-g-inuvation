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
 * @since 06-08-2015
 */
@VisitorInfo(hooks = {"components", "componentsCopy", "closed"})
public class Interface extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount("[" + desc("InterfaceComponent")) == 2 && cn.fieldCount() == 3;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0 && fn.desc.equals("[" + desc("InterfaceComponent"))) {
                addHook((fn.access & ACC_PUBLIC) != 0 ? new FieldHook("components", fn) : new FieldHook("componentsCopy", fn));
            }
        }
        add("closed", cn.getField(null, "Z"));
    }
}

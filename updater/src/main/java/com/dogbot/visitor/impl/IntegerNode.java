/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 07-08-2015
 */
@VisitorInfo(hooks = {"value"})
public class IntegerNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.methodCount() == 1 && cn.fieldCount() == 1
                && cn.constructors().contains("(I)V") && cn.fieldCount("I") == 1;
    }

    @Override
    public void visit() {
        add("value", cn.getField(null, "I"));
    }
}

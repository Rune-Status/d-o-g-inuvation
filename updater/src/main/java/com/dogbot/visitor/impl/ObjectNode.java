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
 * @since 26-07-2015
 * A Node which wraps an Object. Used to wrap npcs, and also used with item definitions
 */
@VisitorInfo(hooks = {"referent"})
public class ObjectNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Node")) && cn.fieldTypeCount() == 1 && cn.fieldCount("Ljava/lang/Object;") == 1;
    }

    @Override
    public void visit() {
        add("referent", cn.getField(null, "Ljava/lang/Object;"));
    }
}

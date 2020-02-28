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
 */
@VisitorInfo(hooks = {"matrix"})
public class Matrix4f extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & ACC_FINAL) != 0 && cn.fieldTypeCount() == 1 && cn.fieldCount(float[].class) == 1
                && cn.getMethod("toString", "()Ljava/lang/String;") != null;
    }

    @Override
    public void visit() {
        add("matrix", cn.getField(null, "[F"));
    }
}

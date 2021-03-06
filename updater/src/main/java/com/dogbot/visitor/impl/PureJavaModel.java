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

import java.util.List;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
@VisitorInfo(hooks = {"xVertices", "yVertices", "zVertices", "xTriangles", "yTriangles", "zTriangles"})
public class PureJavaModel extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Model")) && cn.fieldCount(desc("PureJavaRenderConfiguration")) == 1;
    }

    @Override
    public void visit() {
        List<FieldHook> hooks = Model.findVertices(cn);
        if (hooks != null) {
            hooks.forEach(this::addHook);
        }
        if ((hooks = Model.findTriangles(cn)) != null) {
            hooks.forEach(this::addHook);
        }
    }
}

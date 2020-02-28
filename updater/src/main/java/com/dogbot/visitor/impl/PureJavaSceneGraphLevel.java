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
 * @since 24-07-2015
 */
@VisitorInfo(hooks = {"renderConfiguration"})
public class PureJavaSceneGraphLevel extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("SceneGraphLevel")) && cn.getField(null, desc("PureJavaRenderConfiguration")) != null;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("renderConfiguration", cn.getField(null, desc("PureJavaRenderConfiguration"))));
    }
}

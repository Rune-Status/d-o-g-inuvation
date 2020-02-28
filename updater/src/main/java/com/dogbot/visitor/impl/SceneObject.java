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
 * @since 09-08-2015
 */
@VisitorInfo(hooks = {"getId", "getType", "getOrientation"})
public class SceneObject extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if ((cn.access & ACC_ABSTRACT) == 0) {
            return false;
        }
        ClassNode eventObject = updater.visitor("DynamicGameObject").cn;
        if (eventObject != null) {
            if (eventObject.interfaces.size() == 0) {
                throw new RuntimeException("DynamicGameObject wrong??");
            }
            String name = eventObject.interfaces.get(0);
            return cn.name.equals(name);
        }
        return false;
    }

    @Override
    public void visit() {

    }
}

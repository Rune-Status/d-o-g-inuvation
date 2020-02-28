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
 * @since 21-07-2015
 */
@VisitorInfo(hooks = {"component"})
public class Canvas extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals("java/awt/Canvas");
    }

    @Override
    public void visit() {
        add("component", cn.getField(null, "Ljava/awt/Component;"));
    }
}

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
@VisitorInfo(hooks = {"projectile"})
public class ProjectileNode extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 1 && cn.fieldCount(desc("Projectile")) == 1 && cn.extendsFrom(clazz("DoublyNode"));
    }

    @Override
    public void visit() {
        add("projectile", cn.getField(null, desc("Projectile")));
    }
}

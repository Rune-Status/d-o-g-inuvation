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
 * @since 12-08-2015
 */
@VisitorInfo(hooks = {"referent"})
public class HardCacheReference extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.extendsFrom(clazz("CacheReference")) && cn.fieldCount() == 1 && cn.fieldCount(Object.class) == 1
                && cn.constructors().contains("(Ljava/lang/Object;I)V");
    }

    @Override
    public void visit() {
        add("referent", cn.getField(null, "Ljava/lang/Object;"));
    }
}

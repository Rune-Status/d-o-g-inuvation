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
@VisitorInfo(hooks = {"size"})
public class CacheReference extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 1 && cn.fieldCount(int.class) == 1 && cn.name.equals(updater.visitor("SoftCacheReference").cn.superName);
    }

    @Override
    public void visit() {
        add("size", cn.getField(null, "I"));
    }
}

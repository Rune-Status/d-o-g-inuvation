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
@VisitorInfo(hooks = {"count", "configGroup", "cache"})
public class DefinitionCacheLoader extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("Js5ConfigGroup")) == 1 && cn.fieldCount(desc("Cache")) == 1 && cn.fieldCount("I") == 1;
    }

    @Override
    public void visit() {
        add("count", cn.getField(null, "I"));
        add("cache", cn.getField(null, desc("Cache")));
        add("configGroup", cn.getField(null, desc("Js5ConfigGroup")));
    }
}

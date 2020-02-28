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
 * @since 22-08-2015
 */
@VisitorInfo(hooks = {"index"})
public class Varp extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & ACC_ABSTRACT) != 0 && cn.interfaces.size() == 1 && cn.fieldCount("I") == 1 &&
                cn.fieldCount("Z") == 1 && cn.abnormalFieldCount() > 1 && cn.fieldCount() == 5;
    }

    @Override
    public void visit() {
        add("index", cn.getField(null, "I"));
    }
}

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
 * @since 13-08-2015
 */
@VisitorInfo(hooks = {"text"})
public class OverheadMessage extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 5 && cn.fieldCount(int.class) == 4 && cn.fieldCount(String.class) == 1
                && cn.ownerless() && cn.fieldTypeCountIn(updater.visitor("Mobile").cn) == 1;
    }

    @Override
    public void visit() {
        add("text", cn.getField(null, "Ljava/lang/String;"));
    }
}

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
@VisitorInfo(hooks = {"barList", "definition"})
public class CombatGauge extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("StatusNode")) && cn.fieldCount(desc("StatusList")) == 1;
    }

    @Override
    public void visit() {
        add("barList", cn.getField(null, desc("StatusList")));
        add("definition", cn.getField(null, desc("CombatGaugeDefinition")));
    }
}

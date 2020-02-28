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

import java.util.List;

/**
 * @author Dogerina
 * @since 09-08-2015
 */
@VisitorInfo(hooks = {"particles"})
public class ParticleProvider extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount(List.class) == 2 && cn.fieldCount("[" + desc("Particle")) == 1;
    }

    @Override
    public void visit() {
        add("particles", cn.getField(null, "[" + desc("Particle")));
    }
}

package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

public class HoveredEntity extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("SceneNode")) != 0
                && cn.fieldCount(desc("HoveredEntities")) != 0;
    }

    @Override
    public void visit() {
        add("node", cn.getField(null, desc("SceneNode")));
    }
}

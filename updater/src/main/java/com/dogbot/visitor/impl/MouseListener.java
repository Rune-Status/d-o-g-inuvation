package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

public class MouseListener extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.implement("java/awt/event/MouseListener");
    }

    @Override
    public void visit() {

    }
}

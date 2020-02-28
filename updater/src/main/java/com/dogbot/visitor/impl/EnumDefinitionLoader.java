package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

public class EnumDefinitionLoader extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return false;
    }

    @Override
    public void visit() {

    }
}

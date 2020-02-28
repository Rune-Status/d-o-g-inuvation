package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

public class ItemTableDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.implement(clazz("Definition"))
                && cn.extendsFrom(clazz("DoublyNode"))
                && cn.fieldCount() == 4
                && cn.fieldCount(int.class) == 2
                && cn.fieldCount(int[].class) == 2;
    }

    @Override
    public void visit() {

    }
}

package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

public class MobileAnimator extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Animator")) && cn.fieldCount("Z") == 1;
    }

    @Override
    public void visit() {

    }
}

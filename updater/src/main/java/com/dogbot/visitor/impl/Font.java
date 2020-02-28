package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

@VisitorInfo(hooks = {"specification"})
public class Font extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("RenderConfiguration")) == 1
                && cn.fieldCount(desc("FontSpecification")) == 1
                && (cn.access & ACC_ABSTRACT) > 0
                && cn.fieldCount() == 2;
    }

    @Override
    public void visit() {
        add("specification", cn.getField(null, desc("FontSpecification")));
    }
}

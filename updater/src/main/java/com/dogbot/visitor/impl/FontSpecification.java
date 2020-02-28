package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

@VisitorInfo(hooks = {"glyphSpacing"})
public class FontSpecification extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(int.class) > 5
                && cn.fieldCount(int.class) < 15
                && cn.fieldCount(byte[].class) >= 3
                && cn.fieldCount(byte[][].class) == 1
                && cn.ownerless();
    }

    @Override
    public void visit() {
        add("glyphSpacing", cn.getField(null, "[[B"));
    }
}

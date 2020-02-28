package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

@VisitorInfo(hooks = "animator")
public class MobileSpotAnimation extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("Animator")) == 1
                && cn.fieldCount(int.class) == 4
                && cn.fieldCount() == 5
                && cn.ownerless();
    }

    @Override
    public void visit() {
        add("animator", cn.getField(null, desc("Animator")));
    }
}

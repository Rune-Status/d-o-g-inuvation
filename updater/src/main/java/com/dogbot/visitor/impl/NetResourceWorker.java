package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

@VisitorInfo(hooks = {"status", "errors"})
public class NetResourceWorker extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & ACC_ABSTRACT) > 0
                && cn.fieldCount(desc("DoublyNodeQueue")) == 4
                && cn.fieldCount(desc("Buffer")) == 3;
    }

    @Override
    public void visit() {

    }
}

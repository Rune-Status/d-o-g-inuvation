package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

import java.util.List;

@VisitorInfo(hooks = {"elements"})
public class TestHookContainer extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 4
                && cn.fieldCount(List.class) == 1
                && cn.fieldCount(int.class) == 2
                && cn.fieldCount(boolean.class) == 1;
    }

    @Override
    public void visit() {
        add("elements", cn.getField(null, "Ljava/util/List;"));
    }
}

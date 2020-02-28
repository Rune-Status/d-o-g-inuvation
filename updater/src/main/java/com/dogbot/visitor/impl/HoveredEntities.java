package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

import java.util.List;

@VisitorInfo(hooks = {"list"})
public class HoveredEntities extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount(List.class) == 1
                && cn.fieldCount(int.class) == 2 && cn.fieldCount(boolean.class) == 1;
    }

    @Override
    public void visit() {
        add("list", cn.getField(null, "Ljava/util/List;"));
    }
}

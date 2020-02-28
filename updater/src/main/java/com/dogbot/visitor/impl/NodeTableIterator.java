package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by Inspiron on 26/12/2016.
 */
@VisitorInfo(hooks = {"index", "table"})
public class NodeTableIterator extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.implement("java/util/Iterator") && cn.fieldCount(desc("NodeTable")) == 1
                && cn.fieldCount(desc("Node")) == 2 && cn.fieldCount(int.class) == 1;
    }

    @Override
    public void visit() {
        add("table", cn.getField(null, desc("NodeTable")));
        add("index", cn.getField(null, "I"));
    }
}

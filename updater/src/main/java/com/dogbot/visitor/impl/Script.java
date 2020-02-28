package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by tobyreynolds98 on 10/02/2017.
 */
@VisitorInfo(hooks = {"instructions", "tables"})
public class Script extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount("[" + desc("ScriptInstruction")) == 1 && cn.fieldCount("[" + desc("NodeTable")) == 1;
    }

    @Override
    public void visit() {
        add("instructions", cn.getField(null, "[" + desc("ScriptInstruction")));
        add("tables", cn.getField(null, "[" + desc("NodeTable")));
    }
}

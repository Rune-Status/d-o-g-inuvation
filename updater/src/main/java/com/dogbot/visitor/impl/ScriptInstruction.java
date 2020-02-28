package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by tobyreynolds98 on 10/02/2017.
 */
@VisitorInfo(hooks = {"opcode"})
public class ScriptInstruction extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(int.class) == 1 && cn.fieldCount(boolean.class) == 1 && cn.fieldCount() == 2
                && cn.fieldCount("L" + cn.name + ";", false) > 50;
    }

    @Override
    public void visit() {
        add("opcode", cn.getField(null, "I"));
    }
}

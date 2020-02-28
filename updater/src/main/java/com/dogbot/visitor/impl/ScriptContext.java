package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by tobyreynolds98 on 07/10/2017.
 */
@VisitorInfo(hooks = {"args", "menuOption"})
public class ScriptContext extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(String.class) == 1
                && cn.fieldCount(desc("InterfaceComponent")) == 2
                && cn.fieldCount(Object[].class) == 1
                && cn.extendsFrom(clazz("Node"));
    }

    @Override
    public void visit() {
        add("args", cn.getField(null, "[Ljava/lang/Object;"));
        add("menuOption", cn.getField(null, "Ljava/lang/String;"));
    }
}

package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

@VisitorInfo(hooks = {"defaultInt", "defaultString"})
public class ParameterDefinition extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.interfaces.contains(clazz("Definition")) && cn.fieldCount(String.class) == 1 && cn.fieldCount(int.class) == 1;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("defaultInt", cn.getField(null, "I")));
        addHook(new FieldHook("defaultString", cn.getField(null, "Ljava/lang/String;")));
    }
}

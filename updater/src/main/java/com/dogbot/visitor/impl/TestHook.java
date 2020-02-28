package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

@VisitorInfo(hooks = {"node", "bool", "isUnderPoint"})
public class TestHook extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.abnormalFieldCount() == 2 && cn.fieldCount() == 3
                && cn.fieldCount(boolean.class) == 1
                && cn.fieldCount(desc("SceneNode")) == 1;
    }

    @Override
    public void visit() {
        add("node", cn.getField(null, desc("SceneNode")));
        add("bool", cn.getField(null, "Z"));

        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("(" + desc("RenderConfiguration"))) {
                addHook(new InvokeHook("isUnderPoint", mn, "(" + desc("RenderConfiguration") + "II)Z"));
                break;
            }
        }
    }
}

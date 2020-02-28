package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by tobyreynolds98 on 10/02/2017.
 */
@VisitorInfo(hooks = {"script", "instructions", "targetMobile", "targetItem", "targetObject", "targetMobileIndex"})
public class ScriptExecutionContext extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("Script")) == 1 && cn.fieldCount("[" + desc("ScriptInstruction")) == 1;
    }

    @Override
    public void visit() {
        add("instructions", cn.getField(null, "[" + desc("ScriptInstruction")));
        add("script", cn.getField(null, desc("Script")));
        add("targetMobile", cn.getField(null, desc("Mobile")));
        add("targetItem", cn.getField(null, desc("ItemPile")));
        add("targetObject", cn.getField(null, desc("SceneObject")));
    }
}

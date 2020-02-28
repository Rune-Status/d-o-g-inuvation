package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by Inspiron on 10/12/2016.
 */
@VisitorInfo(hooks = {"animated"})
public class AnimableObjectNode extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 1 && cn.superName.equalsIgnoreCase(clazz("DoublyNode")) && cn.fieldCount(desc("AnimableObject")) == 1;
    }

    @Override
    public void visit() {
        add("animated", cn.getField(null, desc("AnimableObject")));
    }
}

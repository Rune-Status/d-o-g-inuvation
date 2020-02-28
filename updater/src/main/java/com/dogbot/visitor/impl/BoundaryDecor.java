package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by Inspiron on 08/12/2016.
 */
public class BoundaryDecor extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return (cn.access & ACC_ABSTRACT) > 0 && cn.extendsFrom(clazz("SceneNode")) && cn.abnormalFieldCount() == 1
                && cn.fieldCount(short.class) == 2 && cn.fieldCount(int.class) == 1 && cn.fieldCount() == 4;
    }

    @Override
    public void visit() {

    }
}

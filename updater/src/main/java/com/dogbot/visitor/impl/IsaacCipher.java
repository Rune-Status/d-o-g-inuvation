package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by Inspiron on 23/12/2016.
 */
public class IsaacCipher extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldTypeCountIn(updater.visitor("FrameBuffer").cn) == 1;
    }

    @Override
    public void visit() {

    }
}

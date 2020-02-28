package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by Inspiron on 23/12/2016.
 */
public class Connection extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return updater.visitor("AsyncConnection").cn.superName.equals(cn.name);
    }

    @Override
    public void visit() {

    }
}

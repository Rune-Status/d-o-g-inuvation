package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by tobyreynolds98 on 20/03/2017.
 */
public class ArchiveResourceProvider extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.extendsFrom(clazz("ResourceProvider"));
    }

    @Override
    public void visit() {

    }
}

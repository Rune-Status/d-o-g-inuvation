package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by tobyreynolds98 on 20/03/2017.
 */
public class ResourceProvider extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        ClassNode arch = updater.visitor("Archive").cn;
        return arch != null && arch.fieldCount("L" + cn.name + ";") > 0 && (cn.access & ACC_ABSTRACT) > 0;
    }

    @Override
    public void visit() {

    }
}

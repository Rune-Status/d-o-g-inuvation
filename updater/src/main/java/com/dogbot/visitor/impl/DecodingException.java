package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

@VisitorInfo(hooks = {"message", "cause"})
public class DecodingException extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.extendsFrom("java/lang/RuntimeException")
                && cn.fieldCount(String.class) == 1
                && cn.fieldCount(Throwable.class) == 1;
    }

    @Override
    public void visit() {
        add("message", cn.getField(null, "Ljava/lang/String;"));
        add("cause", cn.getField(null, "Ljava/lang/Throwable;"));
    }
}

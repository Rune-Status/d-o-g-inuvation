package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

import java.net.Socket;

/**
 * Created by Inspiron on 23/12/2016.
 */
@VisitorInfo(hooks = {"socket", "input", "output"})
public class AsyncConnection extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(Socket.class) == 1 && cn.fieldCount(desc("AsyncOutputStream")) == 1
                && cn.fieldCount(desc("AsyncInputStream")) == 1;
    }

    @Override
    public void visit() {
        add("socket", cn.getField(null, "Ljava/net/Socket;"));
        add("input", cn.getField(null, desc("AsyncInputStream")));
        add("output", cn.getField(null, desc("AsyncOutputStream")));
    }
}

package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Inspiron on 23/12/2016.
 */
@VisitorInfo(hooks = {"buffer", "stopped", "thread", "target", "exception"})
public class AsyncOutputStream extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(OutputStream.class) == 1 && cn.fieldCount(IOException.class) == 1;
    }

    @Override
    public void visit() {
        add("buffer", cn.getField(null, "[B"));
        add("stopped", cn.getField(null, "Z"));
        add("thread", cn.getField(null, "Ljava/lang/Thread;"));
        add("target", cn.getField(null, "Ljava/io/OutputStream;"));
        add("exception", cn.getField(null, "Ljava/io/IOException;"));
    }
}

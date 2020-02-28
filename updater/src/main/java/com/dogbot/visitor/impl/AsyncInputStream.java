package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Inspiron on 23/12/2016.
 */
@VisitorInfo(hooks = {"buffer", "thread", "target", "exception"})
public class AsyncInputStream extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(InputStream.class) == 1 && cn.fieldCount(IOException.class) == 1;
    }

    @Override
    public void visit() {
        add("buffer", cn.getField(null, "[B"));
        add("thread", cn.getField(null, "Ljava/lang/Thread;"));
        add("target", cn.getField(null, "Ljava/io/InputStream;"));
        add("exception", cn.getField(null, "Ljava/io/IOException;"));
    }
}

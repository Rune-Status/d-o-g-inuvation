package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by tobyreynolds98 on 21/03/2017.
 */
@VisitorInfo(hooks = {"entries"})
public class LookupTable extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return false; //hooked from Archive
    }

    @Override
    public void visit() {
        add("entries", cn.getField(null, "[I"));
    }
}

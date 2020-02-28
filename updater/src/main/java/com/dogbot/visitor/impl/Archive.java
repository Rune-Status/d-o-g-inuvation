package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * Created by tobyreynolds98 on 20/03/2017.
 */
@VisitorInfo(hooks = {"unpack", "resourceProvider", "packed", "unpacked", "table", "discardUnpacked", "discardPacked"})
public class Archive extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("ReferenceTable")) == 1
                && cn.fieldCount(Object[][].class) == 1
                && cn.fieldCount(Object[].class) == 1;
    }

    @Override
    public void visit() {
        for (MethodNode mn : cn.methods) {
            if (mn.desc.endsWith("Z") && (mn.access & ACC_SYNCHRONIZED) > 0
                    && mn.desc.startsWith("(II[I") && (mn.access & ACC_PUBLIC) == 0) {
                addHook(new InvokeHook("unpack", mn, "(II[I)Z"));
            }
        }
        add("resourceProvider", cn.getField(null, desc("ResourceProvider")));
        add("table", cn.getField(null, desc("ReferenceTable")));
        add("discardPacked", cn.getField(null, "Z"));
        add("discardUnpacked", cn.getField(null, "I"));
        add("packed", cn.getField(null, "[Ljava/lang/Object;"));
        add("unpacked", cn.getField(null, "[[Ljava/lang/Object;"));
    }
}

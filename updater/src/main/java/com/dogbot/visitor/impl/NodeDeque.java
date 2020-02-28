/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * @author Dogerina
 * @since 10-08-2015
 */
@VisitorInfo(hooks = {"head", "tail", "add"})
public class NodeDeque extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.interfaces.contains("java/lang/Iterable") && cn.interfaces.contains("java/util/Collection");
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0 && fn.desc.equals(desc("Node"))) {
                if ((fn.access & ACC_PUBLIC) != 0) {
                    add("tail", fn);
                } else {
                    add("head", fn);
                }
            }
        }
        for (MethodNode mn : cn.methods) {
            if (mn.desc.matches("\\(" + desc("Node") + "(B|S|I|)\\)V")) {//add method
                addHook(new InvokeHook("add", mn, "(" + desc("Node") + ")V"));
            }
        }
    }
}

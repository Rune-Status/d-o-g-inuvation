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
import org.objectweb.casm.Type;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * @author Dogerina
 * @since 09-08-2015
 */
@VisitorInfo(hooks = {"load", "count"})
public class DefinitionLoader extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        ClassNode dcl = updater.visitor("DefinitionCacheLoader").cn;
        return dcl != null && dcl.implement(cn.name);
    }

    @Override
    public void visit() {
        for (MethodNode mn : cn.methods) {
            if (mn.desc.endsWith(desc("Definition"))) { //no need for static access check, java 7 interfaces cant have static methods =)
                addHook(new InvokeHook("load", mn, "(I)" + desc("Definition")));
            } else if (mn.desc.endsWith("I") && Type.getArgumentTypes(mn.desc).length <= 1) {
                addHook(new InvokeHook("count", mn, "()I"));
            }
        }
    }
}

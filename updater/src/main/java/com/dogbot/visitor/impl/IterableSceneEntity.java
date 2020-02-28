/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"current", "next"})
public class IterableSceneEntity extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldTypeCount() == 2 && cn.fieldCount("L" + cn.name + ";") == 1 &&
                cn.fieldCount(desc("SceneEntity")) == 1;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0) {
                if (fn.desc.equals(desc("SceneEntity"))) {
                    addHook(new FieldHook("current", fn));
                } else if (fn.desc.equals("L" + cn.name + ";")) {
                    addHook(new FieldHook("next", fn));
                }
            }
        }
    }
}

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
import org.objectweb.casm.tree.*;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
@VisitorInfo(hooks = {"parameters", "displayMode", "caps"})
public class Direct3DRenderConfiguration extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (!cn.superName.equals(clazz("DirectXRenderConfiguration"))) {
            return false;
        }
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.opcode() == LDC && ((LdcInsnNode) ain).cst.equals("Direct3D")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0) {
                if (fn.desc.endsWith("PARAMETERS;")) {
                    addHook(new FieldHook("parameters", fn));
                } else if (fn.desc.endsWith("DISPLAYMODE;")) {
                    addHook(new FieldHook("displayMode", fn));
                } else if (fn.desc.endsWith("CAPS;")) {
                    addHook(new FieldHook("caps", fn));
                }
            }
        }
    }
}

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.LdcInsnNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * @author Dogerina
 * @since 24-07-2015
 */
public class OpenGLXRenderConfiguration extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (!cn.superName.equals(clazz("DirectXRenderConfiguration")) || cn.name.equals(clazz("Direct3DRenderConfiguration"))) {
            return false;
        }
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.opcode() == LDC && !((LdcInsnNode) ain).cst.equals("OpenGL FF")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void visit() {

    }
}

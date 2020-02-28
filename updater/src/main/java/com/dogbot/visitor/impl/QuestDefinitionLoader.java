package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldInsnNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * Created by Inspiron on 23/12/2016.
 */
public class QuestDefinitionLoader extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        boolean found = false;
        loop:
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("<init>")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.opcode() == GETSTATIC) {
                        FieldInsnNode fin = (FieldInsnNode) ain;
                        FieldHook hook = updater.visitor("Client").getFieldHook("questConfigGroup");
                        if (hook != null && fin.owner.equals(hook.clazz) && fin.name.equals(hook.field)) {
                            found = true;
                            break loop;
                        }
                    }
                }
            }
        }
        return found;
    }

    @Override
    public void visit() {

    }
}

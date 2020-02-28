package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.IntInsnNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * Created by Inspiron on 08/12/2016.
 */
public class Boundary extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        boolean b = (cn.access & ACC_ABSTRACT) > 0 && cn.extendsFrom(clazz("SceneNode")) && cn.abnormalFieldCount() == 1
                && cn.fieldCount(short.class) == 1 && cn.fieldCount(int.class) == 1 && cn.fieldCount() == 3;
        boolean[] test = new boolean[3];
        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("([L") && mn.desc.endsWith("I")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.opcode() != BIPUSH) {
                        continue;
                    }
                    IntInsnNode iin = (IntInsnNode) ain;
                    if (iin.operand == 32) {
                        test[0] = true;
                    } else if (iin.operand == 64) {
                        test[1] = true;
                    } else if (iin.operand == 16) {
                        test[2] = true;
                    }
                }
            }
        }
        return b && test[0] && test[1] && test[2];
    }

    @Override
    public void visit() {

    }
}

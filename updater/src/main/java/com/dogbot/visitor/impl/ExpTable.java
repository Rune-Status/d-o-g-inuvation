package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.LdcInsnNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * Created by Inspiron on 23/12/2016.
 */
@VisitorInfo(hooks = {"table"})
public class ExpTable extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        boolean found = false;
        loop:
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.opcode() == LDC) {
                    Object ldc = ((LdcInsnNode) ain).cst;
                    if (ldc instanceof String) {
                        String value = (String) ldc;
                        if (value.toLowerCase().contains("negative xp")) {
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
        addHook(new FieldHook("table", cn.getField(null, "[I")));
    }
}

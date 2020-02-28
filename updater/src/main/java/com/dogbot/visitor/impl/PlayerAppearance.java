package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

import java.lang.reflect.Modifier;

@VisitorInfo(hooks = {"transformedNpcId", "equipment"})
public class PlayerAppearance extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(int.class) >= 2 && cn.fieldCount(int[].class) >= 2
                && cn.fieldCount(long.class) >= 2 && cn.fieldCount(boolean.class) >= 1
                && cn.ownerless() && cn.abnormalFieldCount() == 1;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            int acc = fn.access;
            if ((acc & ACC_STATIC) == 0 && (acc & ACC_PUBLIC) != 0 && fn.desc.equals("I")) {
                addHook(new FieldHook("transformedNpcId", fn));
            } else if ((acc & ACC_STATIC) == 0 && fn.desc.equals("[I") && !Modifier.isPublic(acc)) {
                addHook(new FieldHook("equipment", fn));
            }
        }
    }
}

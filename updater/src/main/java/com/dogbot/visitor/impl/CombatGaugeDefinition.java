package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import com.dogbot.visitor.constraint.OpcodeParsingVisitor;
import org.objectweb.casm.commons.cfg.tree.util.TreeBuilder;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

@VisitorInfo(hooks = {"field2", "field3", "field4", "field5", "field7", "field8", "field9", "field10", "field11", "field12", "field13"})
public class CombatGaugeDefinition extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount("I") == 11 && cn.fieldTypeCount() == 2 && cn.superName.contains("Object");
    }

    @Override
    public void visit() {
        Map<Integer, FieldHook> hooks = new HashMap<>();
        for (int i = 2; i < 14; i++) {
            if (i == 6) continue;
            hooks.put(i, createRaw("field" + i, "I"));
        }

        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("(" + desc("Buffer") + "I")) {
                TreeBuilder.build(mn).accept(new OpcodeParsingVisitor(this, hooks));
            }
        }
    }
}

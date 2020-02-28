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

@VisitorInfo(hooks = {"elements", "parameters", "keys", "defaultString", "defaultValue", "size"})
public class EnumDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(Object[].class) == 1
                && cn.fieldCount(Map.class) == 1
                && cn.fieldCount(HashMap.class) == 1;
    }

    @Override
    public void visit() {
        add("defaultString", cn.getField(null, "Ljava/lang/String;"));
        add("elements", cn.getField(null, "[Ljava/lang/Object;"));
        add("parameters", cn.getField(null, "Ljava/util/Map;"));
        add("keys", cn.getField(null, "Ljava/util/HashMap;"));

        Map<Integer, FieldHook> hooks = new HashMap<>();
        hooks.put(4, createRaw("defaultValue", "I"));
        hooks.put(5, createRaw("size", "I"));

        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("(" + desc("Buffer") + "I")) {
                TreeBuilder.build(mn).accept(new OpcodeParsingVisitor(this, hooks));
            }
        }
    }
}

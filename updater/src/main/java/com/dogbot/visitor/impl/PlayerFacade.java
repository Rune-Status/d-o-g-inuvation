/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"skills", "varps", "getVarpValue", "getVarpBit", "getVarpBitValue"})
public class PlayerFacade extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("Varps")) == 1 && cn.fieldCount("[" + desc("Skill")) == 1;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("skills", cn.getField(null, "[" + desc("Skill"))));
        addHook(new FieldHook("varps", cn.getField(null, desc("Varps"))));
        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0) {
                if (mn.desc.startsWith("(" + desc("Varp"))) {
                    addHook(new InvokeHook("getVarpValue", mn, "(" + desc("Varp") + ")I"));
                } else if (mn.desc.startsWith("(" + desc("VarpBit"))) {
                    addHook(new InvokeHook("getVarpBitValue", mn, "(" + desc("VarpBit") + ")I"));
                } else if (mn.desc.endsWith(desc("VarpBit"))) {
                    addHook(new InvokeHook("getVarpBit", mn, "(I)" + desc("VarpBit")));
                }
            }
        }
    }
}

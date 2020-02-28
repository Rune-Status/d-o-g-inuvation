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
 * @since 06-08-2015
 */
@VisitorInfo(hooks = {"angle"})
public class Rotation extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        boolean fieldCount = cn.fieldCount(int.class) == 2 && cn.fieldAccessCount(ACC_PUBLIC) == 1;
        if (!fieldCount) {
            return false;
        }
        ClassNode character = nodeFor(Player.class);
        if (character != null) {
            character = updater.classnodes.get(character.superName);
            if (character != null) {
                return character.fieldCount("L" + cn.name + ";") == 3;
            }
        }
        return false;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0 && fn.desc.equals("I")) {
                if ((fn.access & ACC_PUBLIC) != 0) {
                    addHook(new FieldHook("angle", fn));
                } else {
                    //TODO find out what the other field is?
                }
            }
        }
    }
}

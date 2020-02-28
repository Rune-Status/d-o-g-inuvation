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
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldInsnNode;
import org.objectweb.casm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"experience", "level", "currentLevel", "maxed", "levelData"})
public class Skill extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount("I") == 3 && cn.fieldCount("Z") >= 1 && cn.fieldCount() <= 5
                && putCountInConstructors(cn) >= 3;
    }

    private int putCountInConstructors(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("<init>") && mn.desc.contains("Z")) {
                int count = 0;
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.opcode() == PUTFIELD) {
                        count++;
                    }
                }
                if (count > 0) {
                    return count;
                }
            }
        }
        return 0;
    }

    @Override
    public void visit() {
        add("levelData", cn.getField(null, desc("SkillLevel")));
        add("maxed", cn.getField(null, "Z")); //200m xp
        visitLocalMethodIf(new Hooks(), m -> m.name.equals("<init>"));
    }

    private class Hooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.owner.name.equals("<init>") && block.count(PUTFIELD) > 0) {
                List<FieldInsnNode> integers = new ArrayList<>();
                for (AbstractInsnNode ain : block.follow().instructions) {
                    if (ain.opcode() == PUTFIELD && ((FieldInsnNode) ain).desc.equals("I")) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) ain;
                        integers.add(fieldInsnNode);
                    }
                }
                if (integers.size() >= 3) {
                    addHook(new FieldHook("experience", integers.get(0)));
                    addHook(new FieldHook("level", integers.get(1)));
                    addHook(new FieldHook("currentLevel", integers.get(2)));
                    lock.set(true);
                }
            }
        }
    }
}

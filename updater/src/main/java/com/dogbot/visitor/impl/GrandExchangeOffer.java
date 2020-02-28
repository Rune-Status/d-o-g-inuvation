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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dogerina
 * @since 13-08-2015
 */
@VisitorInfo(hooks = {"state", "itemPrice", "itemQuantity", "itemId", "transferredWealth", "transferedQuantity"})
public class GrandExchangeOffer extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldTypeCount() == 2 && cn.fieldCount("I") == 5 && cn.fieldCount("B") == 1;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("state", cn.getField(null, "B")));
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
                if (integers.size() >= 5) {
                    addHook(new FieldHook("itemId", integers.get(0)));
                    addHook(new FieldHook("itemPrice", integers.get(1)));
                    addHook(new FieldHook("itemQuantity", integers.get(2)));
                    addHook(new FieldHook("transferedQuantity", integers.get(3)));
                    addHook(new FieldHook("transferredWealth", integers.get(4)));
                    lock.set(true);
                }
            }
        }
    }
}

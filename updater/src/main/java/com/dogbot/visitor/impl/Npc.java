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
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.ArithmeticNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 26-07-2015
 */
@VisitorInfo(hooks = {"name", "definition", /*"combatLevel"*/})
public class Npc extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Mobile")) && cn.fieldCount("Ljava/lang/String;") == 1 && cn.fieldCount("I") > 6;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("name", cn.getField(null, "Ljava/lang/String;")));
        add("definition", cn.getField(null, desc("NpcDefinition")));
        visitMethodIf(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitOperation(ArithmeticNode an) {
                        if (an.opcode() == ISUB && an.hasChild(ICONST_1)) {
                            FieldMemberNode fmn = (FieldMemberNode) an.layer(ARRAYLENGTH, GETFIELD);
                            if (fmn != null) {
                                fmn = (FieldMemberNode) an.parent().layer(IMUL, GETFIELD);
                                if (fmn == null) {
                                    return;
                                }
                                FieldHook fh = new FieldHook("queueSize", fmn.fin());
                                fh.clazz = cn.superName;
                                updater.visitor("Mobile").addHook(fh);
                            }
                        }
                    }
                });
            }
        }, m -> m.desc.startsWith("(L") && m.desc.contains(";I"));
    }
}

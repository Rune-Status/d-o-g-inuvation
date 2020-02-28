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
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 07-08-2015
 */
@VisitorInfo(hooks = {"rank", "world", "name"})
public class FriendsChatMember extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 6
                && cn.fieldCount(String.class) == 4
                && cn.fieldCount(byte.class) == 1
                && cn.fieldCount(int.class) == 1;
    }

    @Override
    public void visit() {
        add("rank", cn.getField(null, "B"));
        add("world", cn.getField(null, "I"));

        visitMethodIf(new Name(), e -> e.desc.startsWith("(" + desc("InterfaceComponent") + desc("Interface") + desc("ScriptExecutionContext")) && e.desc.endsWith(")V"));
    }

    public class Name extends BlockVisitor {
        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.getting() && fmn.owner().equals(cn.name)) {
                        addHook(new FieldHook("name", fmn.fin()));
                    }
                }
            });
        }
    }
}

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
import org.objectweb.casm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

/**
 * @author Dogerina
 * @since 01-08-2015
 */
@VisitorInfo(hooks = {"checksum", "entries", "children", "entryHashes"})
public class ReferenceTable extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldTypeCount() <= 8
                && cn.fieldCount(int[][].class) == 2
                && cn.fieldCount(byte[][].class) == 1;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Checksum(), m -> m.name.equals("<init>"));
        visitLocalMethodIf(new Decode(), m -> !m.name.equals("<init>") && m.desc.startsWith("([B"));
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0) {
                if (fn.desc.startsWith("L") && fn.desc.endsWith(";")) {
                    addHook(new FieldHook("entries", fn));
                    ClassNode cn = updater.classnodes.get(fn.desc.replaceFirst("L", "").replaceFirst(";", ""));
                    if (cn != null) {
                        updater.visitor("LookupTable").cn = cn;
                    }
                } else if (fn.desc.startsWith("[L")) {
                    addHook(new FieldHook("children", fn));
                } else if (fn.desc.startsWith("[[B")) {
                    addHook(new FieldHook("entryHashes", fn));
                }
            }
        }
    }

    private class Checksum extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTFIELD) {
                        MethodMemberNode mmn = (MethodMemberNode) fmn.layer(IMUL, INVOKESTATIC);
                        if (mmn != null && mmn.desc().startsWith("([BI") && mmn.desc().endsWith("I")) {
                            addHook(new FieldHook("checksum", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Decode extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {

        }
    }
}

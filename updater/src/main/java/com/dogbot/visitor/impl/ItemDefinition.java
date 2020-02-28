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
import com.dogbot.visitor.constraint.OpcodeParsingVisitor;
import org.objectweb.casm.Opcodes;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.VariableNode;
import org.objectweb.casm.commons.cfg.tree.util.TreeBuilder;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;
import org.objectweb.casm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogerina
 * @since 22-08-2015
 */
@VisitorInfo(hooks = {"loader", "parameters", "name", "actions", "groundActions", "stackable",
        "noteId", "noteTemplateId", "borrowedId", "borrowedTemplateId", "id"})
public class ItemDefinition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.interfaces.contains(clazz("Definition")) && cn.fieldCount(String.class) == 2
                && cn.fieldCount(String[].class) == 2 && cn.fieldCount(byte[].class) == 3;
    }

    @Override
    public void visit() {
        add("loader", cn.getField(null, desc("DefinitionLoader")));
        add("parameters", cn.getField(null, desc("NodeTable")));
        add("name", cn.getPublicField(null, "Ljava/lang/String;"));

        visitLocalMethodIf(new Constructor(), m -> m.name.equals("<init>"));
        visitMethodIf(new Actions(), e -> e.desc.startsWith("(L") && e.desc.contains(desc("ItemDefinition") + desc("ItemDefinition")));

        for (FieldNode fn : cn.fields) {
            if (!fn.name.equals(getFieldHook("actions").field)) {
                if (fn.desc.equals("[Ljava/lang/String;")) {
                    add("groundActions", fn, "[Ljava/lang/String;");
                    break;
                }
            }
        }

        Map<Integer, FieldHook> hooks = new HashMap<>();
        hooks.put(11, createRaw("stackable", "I"));
        hooks.put(97, createRaw("noteId", "I"));
        hooks.put(98, createRaw("noteTemplateId", "I"));
        hooks.put(121, createRaw("borrowedId", "I"));
        hooks.put(122, createRaw("borrowedTemplateId", "I"));

        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("(" + desc("Buffer") + "I")) {
                TreeBuilder.build(mn).accept(new OpcodeParsingVisitor(this, hooks));
            }
        }
    }

    private class Actions extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitAny(AbstractNode n) {
                    if (n.opcode() == Opcodes.ANEWARRAY) {
                        FieldMemberNode node = (FieldMemberNode) n.preLayer(Opcodes.PUTFIELD);
                        if (node.putting() && node.fin().desc.equals("[Ljava/lang/String;")) {
                            addHook(new FieldHook("actions", node.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Constructor extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitVariable(VariableNode vn) {
                    if (!vn.hasParent()) {
                        return;
                    }
                    AbstractNode ok = vn.opcode() == ALOAD ? vn.parent() : vn.parent().parent();
                    if (!(ok instanceof FieldMemberNode)) {
                        return;
                    }
                    FieldMemberNode fmn = (FieldMemberNode) ok;
                    switch (vn.var()) {
                        case 1:
                            addHook(new FieldHook("id", fmn.fin()));
                            lock.set(true);
                            break;
                    }
                }
            });
        }
    }
}

package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

import java.lang.reflect.Modifier;

@VisitorInfo(hooks = {"mask", "defaultInt"})
public class InterfaceComponentDefinition extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldTypeCount() == 1 && cn.fieldCount("I") == 2 && cn.superName.equals(clazz("Node")) && cn.methodCount() > 7;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Mask(), e -> e.desc.endsWith(")Z"));
        FieldHook hook = this.getFieldHook("mask");
        for (FieldNode fn : cn.fields) {
            if (!Modifier.isStatic(fn.access) && hook != null && !hook.field.equals(fn.name)) {
                add("defaultInt", fn);
            }
        }
    }

    public class Mask extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals("I")) {
                        addHook(new FieldHook("mask", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }
}

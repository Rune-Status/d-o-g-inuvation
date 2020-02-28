package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.VariableNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * Created by Inspiron on 26/12/2016.
 */
@VisitorInfo(hooks = {"uid", "state"})
public class InterfaceNode extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        if (cn.fieldCount() == 2 && cn.fieldCount(int.class) == 2) if (cn.extendsFrom(clazz("Node"))) {
            for (ClassNode c : updater.classnodes.values()) {
                for (MethodNode mn : c.methods) {
                    if ((mn.access & ACC_STATIC) != 0 && mn.desc.startsWith("(IL" + cn.name + ";[IZ")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new BlockVisitor() {

            private int added = 0;

            @Override
            public boolean validate() {
                return added < 2;
            }

            @Override
            public void visit(Block block) {
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
                                addHook(new FieldHook("uid", fmn.fin()));
                                added++;
                                break;
                            case 2:
                                addHook(new FieldHook("state", fmn.fin()));
                                added++;
                                break;
                        }
                    }
                });
            }
        }, m -> m.name.equals("<init>") && m.desc.equals("(II)V"));
    }
}

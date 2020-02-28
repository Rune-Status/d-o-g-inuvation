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
import org.objectweb.casm.Type;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.query.InsnQuery;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.*;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldInsnNode;
import org.objectweb.casm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dogerina
 * @since 22-07-2015
 */
@VisitorInfo(hooks = {"modeId", "hashtable", "getGraphicsCard", "worldToScreen"})
public class RenderConfiguration extends GraphVisitor {

    public static FieldHook findMatrix4f(GraphVisitor gv) {
        InvokeHook hook = (InvokeHook) gv.hooks.get("update");
        if (hook == null) {
            return null;
        }
        MethodNode mn = gv.cn.getMethod(hook.method, hook.desc);
        if (mn == null) {
            throw new RuntimeException("???");
        }
        for (AbstractInsnNode ain : mn.instructions.toArray()) {
            if (ain instanceof FieldInsnNode) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (fin.opcode() == GETFIELD && fin.desc.equals(gv.desc("Matrix4f"))) {
                    return new FieldHook("matrix4f", fin);
                }
            }
        }
        return null;
    }

    public static InvokeHook findUpdate(GraphVisitor gv) {
        AtomicReference<InvokeHook> hookRef = new AtomicReference<>();
        gv.visitLocalMethodIf(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitOperation(ArithmeticNode an) {
                        if (an.opcode() == IOR) {
                            NumberNode nn = an.firstNumber();
                            if (nn != null && nn.number() == 16) {
                                hookRef.set(new InvokeHook("update", block.owner, "(IIIIII)V"));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }, m -> m.desc.endsWith("I") && m.desc.startsWith("(IIIIII") && Type.getArgumentTypes(m.desc).length < 8);
        return hookRef.get();
    }

    public static List<FieldMemberNode> findHooks(GraphVisitor gv) {
        List<FieldMemberNode> hooks = new ArrayList<>(4);
        gv.visitLocalMethodIf(new BlockVisitor() {

            private int added = 0;

            @Override
            public boolean validate() {
                return added < 4;
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visit(AbstractNode n) {
                        if (n.opcode() == FASTORE) {
                            ArithmeticNode add = n.firstOperation();
                            if (add != null && add.opcode() == FADD) {
                                FieldMemberNode abs = add.firstField();
                                if (abs != null && abs.opcode() == GETFIELD && abs.desc().equals("F")) {
                                    FieldMemberNode multi = (FieldMemberNode) add.layer(FDIV, FMUL, GETFIELD);
                                    if (multi != null && multi.desc().equals("F")) {
                                        hooks.add(abs);
                                        hooks.add(multi);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }, mn -> mn.desc.endsWith("V") && mn.desc.startsWith("(FFF[F"));
        return hooks.size() < 4 ? null : hooks;
    }

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && (cn.access & ACC_ABSTRACT) != 0 && cn.fieldCount("I") >= 2 && cn.fieldCount(Hashtable.class) == 1;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("hashtable", cn.getField(null, "Ljava/util/Hashtable;")));
        visitMethodIf(new Mode(), m -> m.desc.startsWith("(" + desc()));
        String desc = desc("GraphicsCard");
        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0) {
                if (mn.desc.endsWith(desc)) {
                    addHook(new InvokeHook("getGraphicsCard", mn, "()" + desc));
                }
            }
        }
    }

    private class Mode extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            if (block.count(new InsnQuery(IOR)) == 2) {
                block.tree().accept(new NodeVisitor() {
                    public void visitVariable(VariableNode vn) {
                        if (vn.opcode() == LSTORE) {
                            FieldMemberNode fmn = (FieldMemberNode) vn.layer(I2L, IOR, ISHL, IMUL, GETFIELD);
                            if (fmn != null && fmn.owner().equals(cn.name)) {
                                addHook(new FieldHook("modeId", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                });
            }
        }
    }
}

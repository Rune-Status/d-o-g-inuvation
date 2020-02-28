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

@VisitorInfo(hooks = {"dynamic"})
public class SceneFormat extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount() == 2
                && cn.fieldCount(boolean.class) == 2
                && updater.visitor("Scene").cn.fieldCount("L" + cn.name + ";") == 2;
    }

    @Override
    public void visit() {
        GraphVisitor scene = updater.visitor("Scene");
        scene.visit(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() == PUTFIELD && fmn.desc().equals("L" + cn.name + ";")) {
                            VariableNode vn = (VariableNode) fmn.layer(GETFIELD, ALOAD);
                            if (vn != null && vn.var() == 0) {
                                FieldMemberNode assign = (FieldMemberNode) vn.parent();
                                if (assign.desc().equals("L" + cn.name + ";")) {
                                    scene.addHook(new FieldHook("format", fmn.fin()));
                                    lock.set(true);
                                }
                            }
                        }
                    }
                });
            }
        });

        visitLocalMethodIf(new BlockVisitor() {

            private int added = 0;

            @Override
            public boolean validate() {
                return added < 1;
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    public void visitVariable(VariableNode vn) {
                        if (!vn.hasParent()) {
                            return;
                        }
                        AbstractNode ok = vn.parent();
                        if (!(ok instanceof FieldMemberNode)) {
                            return;
                        }
                        FieldMemberNode fmn = (FieldMemberNode) ok;
                        switch (vn.var()) {
                            case 1:
                                addHook(new FieldHook("dynamic", fmn.fin()));
                                added++;
                                break;
                            case 2:
                                //                    addHook(new FieldHook("idk", fmn.fin()));
                                //                  added++;
                                break;
                        }
                    }
                });
            }
        }, m -> m.name.equals("<init>"));
    }
}

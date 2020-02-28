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
import org.objectweb.casm.commons.cfg.tree.node.NumberNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

import java.util.List;

/**
 * @author Dogerina
 * @since 07-08-2015
 */
@VisitorInfo(hooks = {"size", "index"})
public class Js5ConfigGroup extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount("L" + cn.name + ";", false) > 70 && cn.fieldCount(int.class) == 2
                && cn.constructors().contains("(II)V") && cn.constructors().contains("(I)V");
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0 && fn.desc.equals("I")) {
                if ((fn.access & ACC_PUBLIC) != 0) {
                    addHook(new FieldHook("index", fn));
                } else {
                    addHook(new FieldHook("size", fn));
                }
            }
        }
        GraphVisitor client = updater.visitor("Client");
        visitMethod(cn.getMethod("<clinit>", null), new BlockVisitor() {

            private int added = 0;

            @Override
            public boolean validate() {
                return added < 3;
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() == PUTSTATIC) {
                            MethodMemberNode mmn = fmn.firstMethod();
                            if (mmn != null && mmn.name().equals("<init>")) {
                                List<NumberNode> numericChildren = mmn.findNumericChildren();
                                if (numericChildren != null) {
                                    if (numericChildren.size() == 1) {
                                        int num = numericChildren.get(0).number();
                                        if (num == 5) {
                                            client.addHook(new FieldHook("itemTableConfigGroup", fmn.fin()));
                                        } else if (num == 35) {
                                            client.addHook(new FieldHook("questConfigGroup", fmn.fin()));
                                            //hack xd
                                            for (ClassNode cn : updater.classnodes.values()) {
                                                GraphVisitor qdl = updater.visitor("QuestDefinitionLoader");
                                                if (qdl.validate(cn)) {
                                                    qdl.cn = cn;
                                                    break;
                                                }
                                            }
                                        } else if (num == 69) {
                                            client.addHook(new FieldHook("varpBitConfigGroup", fmn.fin()));
                                        }
                                    } else if (numericChildren.size() == 2) {
                                        int num = numericChildren.get(0).number();
                                        int shift = numericChildren.get(1).number();
                                        if (num == 10 && shift == 8) {
                                            client.addHook(new FieldHook("itemDefinitionConfigGroup", fmn.fin()));
                                        } else if (num == 6 && shift == 8) {
                                            client.addHook(new FieldHook("objectDefinitionConfigGroup", fmn.fin()));
                                        } else if (num == 9 && shift == 7) {
                                            client.addHook(new FieldHook("npcDefinitionConfigGroup", fmn.fin()));
                                        } else if (num == 12 && shift == 7) {
                                            client.addHook(new FieldHook("animationConfigGroup", fmn.fin()));
                                        } else if (num == 8 && shift == 8) {
                                            client.addHook(new FieldHook("enumConfigGroup", fmn.fin()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}

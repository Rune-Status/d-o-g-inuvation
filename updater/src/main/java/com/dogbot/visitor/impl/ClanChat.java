package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.*;
import org.objectweb.casm.tree.ClassNode;

@VisitorInfo(hooks = {"channelName", "parameters", "hash", "memberIndices", "memberCount", "memberNames", "varps", "memberRanks", "ownerIndex"})
public class ClanChat extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(boolean[].class) == 1
                && cn.fieldCount(byte[].class) == 1
                && cn.fieldCount(int[].class) == 3
                && cn.fieldCount(String[].class) == 2;
    }

    @Override
    public void visit() {
        add("parameters", cn.getField(null, desc("NodeTable")));
        add("hash", cn.getField(null, "J"));
        add("channelName", cn.getField(null, "Ljava/lang/String;"));

        visitLocalMethodIf(new Indices(), m -> m.desc.endsWith("[I") && m.parameters() < 2);
        visitLocalMethodIf(new Names(), m -> m.desc.endsWith(")I") && m.desc.startsWith("(Ljava/lang/String;"));
        visitLocalMethodIf(new Varps(), m -> m.desc.endsWith(")I") && m.desc.startsWith("(III"));
        visitLocalMethodIf(new Ranks(), m -> m.desc.endsWith("V") && m.parameters() < 2);
    }

    private class Indices extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.owner().equals(cn.name) && fmn.opcode() == GETFIELD) {
                        if (fmn.desc().equals("[I")) {
                            addHook(new FieldHook("memberIndices", fmn));
                        } else if (fmn.desc().equals("I")) {
                            addHook(new FieldHook("memberCount", fmn));
                        }
                    }
                }
            });
        }
    }

    private class Names extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.name().equals("equals")) {
                        FieldMemberNode expr = (FieldMemberNode) mmn.layer(AALOAD, GETFIELD);
                        if (expr != null && expr.desc().equals("[Ljava/lang/String;")) {
                            addHook(new FieldHook("memberNames", expr));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Varps extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IUSHR) {
                        FieldMemberNode expr = (FieldMemberNode) an.layer(IAND, IALOAD, GETFIELD);
                        if (expr != null) {
                            addHook(new FieldHook("varps", expr));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Ranks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 126) {
                        AbstractNode expr = nn.parent();
                        if (expr != null && expr.opcode() == BASTORE) {
                            FieldMemberNode ranks = (FieldMemberNode) expr.layer(GETFIELD);
                            FieldMemberNode owner = (FieldMemberNode) expr.layer(IMUL, GETFIELD);
                            if (ranks == null || owner == null) {
                                return;
                            }
                            addHook(new FieldHook("memberRanks", ranks));
                            addHook(new FieldHook("ownerIndex", owner));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}

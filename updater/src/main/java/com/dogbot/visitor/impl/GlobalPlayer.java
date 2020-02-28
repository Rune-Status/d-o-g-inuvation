package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.ConstantHook;
import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.Type;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.NumberNode;
import org.objectweb.casm.commons.cfg.tree.node.VariableNode;
import org.objectweb.casm.tree.ClassNode;

@VisitorInfo(hooks = {"clanMate", "positionHash", "targetIndex", "orientation"})
public class GlobalPlayer extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(int.class) == 3
                && cn.fieldCount(boolean.class) == 1
                && cn.abnormalFieldCount() == 1
                && cn.fieldAccessCount(ACC_FINAL) == 0;
    }

    @Override
    public void visit() {
        add("clanMate", cn.getField(null, "Z"));
        visitMethodIf(new Hooks(), m -> m.desc.startsWith("(" + desc("FrameBuffer") + "I"));
    }

    private class Hooks extends BlockVisitor {

        private final String local = clazz("Player");
        private final String target = updater.visitor("Mobile").getFieldHook("targetIndex").field;

        private final String read = updater.visitor("FrameBuffer").getHookKey("readBits");

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {

                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (!fmn.desc().equals("I")
                            || fmn.opcode() != PUTFIELD) {
                        return;
                    }

                    if (fmn.owner().equals(cn.name)
                            && fmn.hasChild(AALOAD)) {
                        addHook(new FieldHook("positionHash", fmn));
                    }

                    if (fmn.owner().equals(local)
                            && fmn.name().equals(target)
                            && fmn.desc().equals("I")) {
                        FieldMemberNode expr = (FieldMemberNode) fmn.layer(IMUL, GETFIELD);
                        if (expr != null && expr.owner().equals(cn.name)) {
                            addHook(new FieldHook("targetIndex", expr));
                        }
                    }
                }

                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.owner().equals(local) && mmn.desc().startsWith("(IZ")) {
                        FieldMemberNode arg = (FieldMemberNode) mmn.layer(IMUL, GETFIELD);
                        if (arg != null) {
                            addHook(new FieldHook("orientation", arg));
                        }
                    }

                    if (mmn.key().equals(read)) {
                        NumberNode nn = mmn.firstNumber();
                        VariableNode store = (VariableNode) mmn.preLayer(ISTORE);
                        if (nn != null && store != null && nn.number() == 6) {
                            GraphVisitor vis = updater.visitor("Client");

                            //TODO maybe bad
                            int baseVar = 4;
                            if (Type.getArgumentTypes(mmn.method().desc).length > 2) {
                               // baseVar++;
                            }

                            if (store.var() == baseVar) {
                                vis.addHook(new ConstantHook("gpiBaseX", "Client", store.var()));
                                vis.addHook(new InvokeHook("decodeGlobalPlayer", nn.method(), "omit"));
                            } else if (store.var() == baseVar + 1) {
                                vis.addHook(new ConstantHook("gpiBaseY", "Client", store.var()));
                            }
                        }
                    }
                }
            });
        }
    }
}

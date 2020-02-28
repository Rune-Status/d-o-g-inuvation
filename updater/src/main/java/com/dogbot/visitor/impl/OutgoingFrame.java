package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.TypeNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Inspiron on 23/12/2016.
 */
@VisitorInfo(hooks = {"bufferSize", "size", "buffer", "meta"})
public class OutgoingFrame extends GraphVisitor {

    private static List<String> test = new ArrayList<>();

    static void prep(GraphVisitor buffcon) {
        buffcon.visit(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();//test == null;
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitType(TypeNode tn) {
                        if (tn.opcode() == CHECKCAST) {
                            AbstractNode store = tn.parent();
                            if (store != null) {
                                FieldMemberNode collection = (FieldMemberNode) store.layer(INVOKEVIRTUAL, GETFIELD);
                                if (collection != null && collection.desc().equals(buffcon.desc("NodeDeque"))) {
                                    test.add(tn.type());
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean validate(ClassNode cn) {
        return test.contains(cn.name) && cn.fieldTypeCount() > 1;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0) {
                if (fn.desc.equals("I")) {
                    if ((fn.access & ACC_PUBLIC) == 0) {
                        addHook(new FieldHook("bufferSize", fn));
                    } else {
                        addHook(new FieldHook("size", fn));
                    }
                } else if (fn.desc.startsWith("L")) {
                    if ((fn.access & ACC_PUBLIC) == 0) {
                        addHook(new FieldHook("meta", fn));
                    } else {
                        addHook(new FieldHook("buffer", fn));
                    }
                }
            }
        }
    }
}

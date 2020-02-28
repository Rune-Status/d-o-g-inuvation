package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.ArithmeticNode;
import org.objectweb.casm.commons.cfg.tree.node.NumberNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;

/**
 * Created by Inspiron on 23/12/2016.
 */
@VisitorInfo(hooks = {"cipher", "bitCaret", "readBits"})
public class FrameBuffer extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("Buffer")) && cn.fieldCount() == 2;
    }

    @Override
    public void visit() {
        visitLocalMethodIf(new Bits(), m -> m.desc.startsWith("(I") && m.desc.endsWith("I"));
        add("bitCaret", cn.getField(null, "I"));
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0 && fn.desc.startsWith("L") && fn.desc.endsWith(";")) {
                addHook(new FieldHook("cipher", fn));
                break;
            }
        }
    }

    private class Bits extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitOperation(ArithmeticNode an) {
                    if (an.opcode() == IAND) {
                        NumberNode val = (NumberNode) an.first(BIPUSH);
                        if (val != null && val.number() == 0x7) {
                            addHook(new InvokeHook("readBits", an.method(), "omit"));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }
}

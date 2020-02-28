package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.NumberNode;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by Inspiron on 08/12/2016.
 */
@VisitorInfo(hooks = {"outgoing", "idleTicks", "connection", "frameDeque"})
public class BufferedConnection extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if (cn.fieldCount(desc("Connection")) == 1 && cn.fieldCount(desc("NodeDeque")) == 1 && cn.fieldCount(desc("Buffer")) == 1) {
            super.cn = cn;
            OutgoingFrame.prep(this);
            return true;
        }
        return false;
    }

    @Override
    public void visit() {
        //   add("monitor", cn.getField(null, desc("LatencyMonitor")));
        add("outgoing", cn.getField(null, desc("Buffer")));
        add("connection", cn.getField(null, desc("Connection")));
        add("frameDeque", cn.getField(null, desc("NodeDeque")));
        visitAll(new BlockVisitor() {
            @Override
            public boolean validate() {
                return !lock.get();
            }

            @Override
            public void visit(Block block) {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitNumber(NumberNode nn) {
                        if (nn.number() == 2250) {
                            AbstractNode par = nn.parent();
                            if (par != null) {
                                FieldMemberNode fmn = (FieldMemberNode) par.layer(IMUL, GETFIELD, GETSTATIC);
                                if (fmn != null && fmn.desc().equals("L" + cn.name + ";")) {
                                    fmn = (FieldMemberNode) fmn.parent();
                                    addHook(new FieldHook("idleTicks", fmn.fin()));
                                    lock.set(true);
                                }
                            }
                        }
                    }
                });
            }
        });
    }
}

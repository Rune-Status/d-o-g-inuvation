package org.objectweb.casm.commons.cfg.tree.node;

import org.objectweb.casm.commons.cfg.tree.NodeTree;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.LdcInsnNode;

/**
 * @author Tyler Sedlar
 */
public class ConstantNode extends AbstractNode {

    public ConstantNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    @Override
    public LdcInsnNode insn() {
        return (LdcInsnNode) super.insn();
    }

    public Object cst() {
        return insn().cst;
    }
}

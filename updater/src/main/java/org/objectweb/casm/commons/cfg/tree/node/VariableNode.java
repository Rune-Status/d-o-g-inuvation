package org.objectweb.casm.commons.cfg.tree.node;

import org.objectweb.casm.commons.cfg.tree.NodeTree;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.VarInsnNode;

/**
 * @author Tyler Sedlar
 */
public class VariableNode extends AbstractNode {

    public VariableNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    public int var() {
        return ((VarInsnNode) insn()).var;
    }
}

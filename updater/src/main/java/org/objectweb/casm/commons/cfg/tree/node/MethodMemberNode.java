package org.objectweb.casm.commons.cfg.tree.node;

import org.objectweb.casm.commons.cfg.tree.NodeTree;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.MethodInsnNode;

/**
 * @author Tyler Sedlar
 */
public class MethodMemberNode extends ReferenceNode {

    public MethodMemberNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    public MethodInsnNode min() {
        return (MethodInsnNode) insn();
    }
}

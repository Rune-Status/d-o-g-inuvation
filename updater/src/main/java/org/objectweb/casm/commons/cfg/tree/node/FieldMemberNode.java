package org.objectweb.casm.commons.cfg.tree.node;

import org.objectweb.casm.commons.cfg.tree.NodeTree;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.FieldInsnNode;

/**
 * @author Tyler Sedlar
 */
public class FieldMemberNode extends ReferenceNode {

    public FieldMemberNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    public FieldInsnNode fin() {
        return (FieldInsnNode) insn();
    }

    public boolean getting() {
        return opcode() == GETFIELD || opcode() == GETSTATIC;
    }

    public boolean putting() {
        return opcode() == PUTFIELD || opcode() == PUTSTATIC;
    }
}

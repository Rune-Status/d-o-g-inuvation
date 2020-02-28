package org.objectweb.casm.commons.cfg.tree.node;

import org.objectweb.casm.commons.cfg.tree.NodeTree;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.TypeInsnNode;

/**
 * @author Tyler Sedlar
 */
public class TypeNode extends AbstractNode {

    public TypeNode(NodeTree tree, AbstractInsnNode insn, int collapsed, int producing) {
        super(tree, insn, collapsed, producing);
    }

    public String type() {
        return ((TypeInsnNode) insn()).desc;
    }
}

package org.objectweb.casm.commons.cfg.graph;

import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 */
public class FlowGraph extends Digraph<Block, Block> {

    private final MethodNode mn;

    public FlowGraph(MethodNode mn) {
        this.mn = mn;
    }

    public MethodNode method() {
        return mn;
    }
}

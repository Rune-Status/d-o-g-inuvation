package org.objectweb.casm.commons.cfg.graph;

import org.objectweb.casm.Handle;

/**
 * @author Tyler Sedlar
 */
public class CallGraph extends Digraph<Handle, Handle> {

    public void addMethodCall(Handle source, Handle target) {
        addVertex(target);
        addEdge(source, target);
    }
}

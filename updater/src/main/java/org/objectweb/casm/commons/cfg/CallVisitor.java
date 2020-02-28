package org.objectweb.casm.commons.cfg;

import org.objectweb.casm.Handle;
import org.objectweb.casm.MethodVisitor;
import org.objectweb.casm.commons.cfg.graph.CallGraph;
import org.objectweb.casm.tree.MethodInsnNode;
import org.objectweb.casm.tree.MethodNode;

/**
 * @author Tyler Sedlar
 */
public class CallVisitor extends MethodVisitor {

    public final CallGraph graph = new CallGraph();

    private MethodNode mn;

    public void visit(MethodNode mn) {
        this.mn = mn;
        mn.accept(this);
    }

    @Override
    public void visitMethodInsn(MethodInsnNode min) {
        graph.addMethodCall(mn.handle, new Handle(0, min.owner, min.name, min.desc));
    }
}
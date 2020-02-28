package com.dogbot;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.graph.FlowGraph;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.VariableNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

import java.io.FileInputStream;
import java.util.Map;
import java.util.jar.JarInputStream;

public class OSRSUpdater extends Updater {

    public OSRSUpdater(JarInputStream jarInputStream) {
        super(jarInputStream, new GraphVisitor[]{new GraphVisitor() {
            @Override
            public boolean validate(ClassNode cn) {
                return cn.name.equals("client");
            }

            @Override
            public void visit() {
                this.visitMethodIf(new Destination(), e -> e.desc.matches("\\(IIIILjava/lang/String;Ljava/lang/String;II(I|B|S|)\\)V"));
            }

            class Destination extends BlockVisitor {

                private String[] hooks = {"destinationX", "destinationY"};

                @Override
                public boolean validate() {
                    return !lock.get();
                }

                @Override
                public void visit(Block block) {
                    block.tree().accept(new NodeVisitor() {
                        @Override
                        public void visitField(FieldMemberNode fmn) {
                            VariableNode an = (VariableNode) fmn.layer(IMUL, ILOAD);
                            if (fmn.isStatic() && fmn.putting() && an != null) {
                                int idx = an.var() - 1;
                                if (idx > 0 && idx < hooks.length) {

                                }
                                if (an.var() == 1 || an.var() == 2) {
                                    System.out.println(fmn);
                                }
                            }
                        }
                    });
                }

            }
        }}, false);
    }


    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getHash() {
        return null;
    }

    @Override
    public String getAccessorPrefix() {
        return null;
    }

    @Override
    public String getWrapperPrefix() {
        return null;
    }

    @Override
    public int getRevision(Map<ClassNode, Map<MethodNode, FlowGraph>> graphs) {
        return 0;
    }

    @Override
    public String getModscriptLocation() {
        return null;
    }

    public static void run(JarInputStream stream) throws Exception {
        OSRSUpdater updater = new OSRSUpdater(stream);
        updater.translateStrings = false;
        updater.setRemoveUnusedMethods(true);
        System.gc();
        updater.run();
        updater.modscript(true);
        updater.flush();
    }

    public static void main(String[] args) throws Exception {
        run(new JarInputStream(new FileInputStream("C:\\Users\\mdawg\\Documents\\RSPeer\\cache\\gamepack.jar")));
    }
}

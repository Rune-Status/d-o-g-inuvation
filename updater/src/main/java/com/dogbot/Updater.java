package com.dogbot;

import com.dogbot.hookspec.HookSpec;
import com.dogbot.util.io.printer.DefaultHookPrinter;
import com.dogbot.util.io.printer.HookPrinter;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.constraint.TranslatableStringVisitor;
import com.dogbot.visitor.constraint.multiplier.DefaultInverseVisitor;
import com.dogbot.visitor.constraint.multiplier.InverseVisitor;
import org.rspeer.loader.Crawler;
import org.rspeer.loader.GameConfiguration;
import org.rspeer.loader.GameEnvironment;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.FlowVisitor;
import org.objectweb.casm.commons.cfg.graph.FlowGraph;
import org.objectweb.casm.commons.cfg.transform.UnusedMethodTransform;
import org.objectweb.casm.commons.util.JarArchive;
import org.objectweb.casm.commons.wrapper.ClassFactory;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarInputStream;

/**
 * @author Tyler Sedlar, Dogerina
 */
public abstract class Updater extends Thread implements Runnable {

    public final JarInputStream stream;
    public final StringBuilder builder;
    private final HookPrinter hookPrinter;
    public String hash;
    public JarArchive archive;
    public Map<String, ClassNode> classnodes;
    public GraphVisitor[] visitors;
    public InverseVisitor inverseVisitor;
    // public OpaquePredicateVisitor opaquePredicateVisitor;
    public TranslatableStringVisitor translatableStringVisitor;
    public boolean translateStrings = true;
    public int revision;
    protected Map<ClassNode, Map<MethodNode, FlowGraph>> graphs;
    private boolean removeUnusedMethods = true;
    private boolean print = true;

    public Updater(JarInputStream jarInputStream, GraphVisitor[] visitors, boolean closeOnOld) {
        graphs = new HashMap<>();
        builder = new StringBuilder();
        hookPrinter = new DefaultHookPrinter(this);
        if (jarInputStream == null) {
            GameConfiguration configuration = new GameEnvironment(null);
            configuration.load();
            Crawler crawler = new Crawler(configuration);
            jarInputStream = crawler.getStream();
        }
        this.stream = jarInputStream;
        this.visitors = visitors;
        archive = new JarArchive(stream);
        classnodes = archive.build();
    }

    protected final void setRemoveUnusedMethods(boolean yes) {
        this.removeUnusedMethods = yes;
    }

    public abstract String getType();

    public abstract String getHash();

    public abstract String getAccessorPrefix();

    public abstract String getWrapperPrefix();

    public abstract int getRevision(Map<ClassNode, Map<MethodNode, FlowGraph>> graphs);

    public String getAccessorPackage() {
        String prefix = getAccessorPrefix();
        return prefix.isEmpty() ? "" : prefix.substring(0, prefix.lastIndexOf('/'));
    }

    public String getAccessorParent() {
        String prefix = getAccessorPackage();
        return prefix.isEmpty() ? "" : prefix.substring(0, prefix.lastIndexOf('/'));
    }

    public abstract String getModscriptLocation();

    public void appendLine(String line) {
        builder.append(line).append("\n");
    }

    public GraphVisitor visitor(String visitor) {
        for (GraphVisitor gv : visitors) {
            if (gv.id().equals(visitor)) {
                return gv;
            }
        }
        return null;
    }

    public GraphVisitor visitorForClass(String klass) {
        for (GraphVisitor gv : visitors) {
            if (gv.cn != null && gv.cn.name.equals(klass)) {
                return gv;
            }
        }
        return null;
    }

    public Map<ClassNode, Map<MethodNode, FlowGraph>> graphs() {
        return graphs;
    }

    public byte[] modscript(boolean toFile) {
        List<GraphVisitor> graphVisitors = new ArrayList<>();
        Collections.addAll(graphVisitors, this.visitors);
        try {
            appendLine("\thash " + hash);
            if (print) {
                System.out.println(builder);
            }
            String loc = getModscriptLocation();
            if (loc != null ) {
                byte[] modscript = HookSpec.write(hash, graphVisitors);
                if (toFile) {
                    Files.write(Paths.get(loc), modscript);
                }
                return modscript;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public void run() {
        if (stream == null) {
            return;
        }
        hash = getHash();

        List<ClassNode> remove = new ArrayList<>();
        for (ClassNode classNode : classnodes.values()) {
            if (classNode.name.contains("/")) {
                remove.add(classNode);
            }
        }
        for (ClassNode cn : remove) {
            classnodes.remove(cn.name);
        }
        Map<String, ClassFactory> factories = new HashMap<>();
        for (ClassNode cn : classnodes.values()) {
            factories.put(cn.name, new ClassFactory(cn));
        }
        UnusedMethodTransform umt = new UnusedMethodTransform(factories);
        if (removeUnusedMethods) {
            umt.transform(classnodes);
            if (print) {
                System.out.println(umt);
            }
        }
        for (GraphVisitor gv : visitors) {
            gv.updater = this;
        }
        inverseVisitor = new DefaultInverseVisitor(this, factories);

        //this.opaquePredicateVisitor = new OpaquePredicateVisitor();
        for (ClassNode cn : classnodes.values()) {
            Map<MethodNode, FlowGraph> local = new HashMap<>();
            for (MethodNode mn : cn.methods) {
                FlowGraph graph = new FlowGraph(mn);
                FlowVisitor visitor = new FlowVisitor();
                visitor.accept(mn);
                //opaquePredicateVisitor.accept(mn);
                graph.graph(visitor.graph);
                for (Block block : graph) {
                    block.tree();
                }
                local.put(mn, graph);
            }
            graphs.put(cn, local);
        }

        inverseVisitor.apply();
        inverseVisitor.debug();

        if (translateStrings) {
            this.translatableStringVisitor = new TranslatableStringVisitor(this);
        }
        revision = getRevision(graphs);
        appendLine("\tBuild #" + revision);
        appendLine("");
        long start = System.nanoTime();
        for (GraphVisitor gv : visitors) {
            for (ClassNode cn : classnodes.values()) {
                if (gv.validate(cn)) {
                    gv.cn = cn;
                    break;
                }
            }
        }
        //        appendLine(String.format("\tClass identification time %.2f seconds", (end - start) / 1e9));
        for (GraphVisitor visitor : this.visitors) {
            if (visitor.cn != null) {
                visitor.visit();
            }
        }
        long end = System.nanoTime();
        hookPrinter.writeTo(builder);
        appendLine(String.format("\tTotal time %.2f seconds", (end - start) / 1e9));
    }

    public void setPrint(boolean print) {
        this.print = print;
    }

    public final void flush() {
        archive = null;
        for (Map.Entry<ClassNode, Map<MethodNode, FlowGraph>> entry : graphs.entrySet()) {
            for (Map.Entry<MethodNode, FlowGraph> mEntry : entry.getValue().entrySet()) {
                FlowGraph graph = mEntry.getValue();
                graph.flush();
            }
        }
        graphs.clear();
        graphs = null;
        visitors = null;
    }

    public FlowGraph getGraph(MethodNode mmn) {
        ClassNode cn = classnodes.get(mmn.owner.name);
        Map<MethodNode, FlowGraph> mapped = graphs.get(cn);
        if (mapped == null) {
            return null;
        }
        return mapped.get(cn.getMethod(mmn.name, mmn.desc));
    }

    public MethodNode getMethod(String owner, String name, String desc) {
        ClassNode cn = classnodes.get(owner);
        return cn != null ? cn.getMethod(name, desc) : null;
    }
}

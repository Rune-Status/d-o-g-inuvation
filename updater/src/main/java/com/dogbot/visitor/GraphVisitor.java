package com.dogbot.visitor;

import com.dogbot.Updater;
import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.hookspec.hook.Hook;
import com.dogbot.hookspec.hook.InvokeHook;
import org.objectweb.casm.MethodVisitor;
import org.objectweb.casm.Opcodes;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.graph.FlowGraph;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;
import org.objectweb.casm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class GraphVisitor implements Opcodes {

    public final Map<String, Hook> hooks = new HashMap<>();

    public Updater updater;
    public ClassNode cn = null;
    public FlowGraph graph;

    protected String id = null;

    public abstract boolean validate(ClassNode cn);

    public abstract void visit();

    public final String id() {
        return id != null ? id : (id = getClass().getSimpleName());
    }

    public String iface() {
        return updater.getAccessorPrefix() + id();
    }

    public String clazz(String visitor) {
        try {
            return updater.visitor(visitor).cn.name;
        } catch (NullPointerException e) {
            return "null";
        }
    }

    public ClassNode nodeFor(Class<? extends GraphVisitor> clazz) {
        String klass = clazz(clazz.getSimpleName());
        return klass != null ? updater.classnodes.get(klass) : null;
    }

    public String desc() {
        return desc(id());
    }

    public String desc(String visitor) {
        return "L" + clazz(visitor) + ";";
    }

    public String literalDesc() {
        return desc(id());
    }

    public String literalDesc(String visitor) {
        return "L" + updater.visitor(visitor).id() + ";";
    }

    public String key(String hook) {
        FieldHook fh = (FieldHook) hooks.get(hook);
        return fh != null ? fh.clazz + "." + fh.field : null;
    }

    protected FieldHook createRaw(String name, String desc) {
        return new FieldHook(name, null, null, desc);
    }

    public final void addHook(Hook hook) {
        if (hook.name == null) {
            return;
        }
        Hook hook0 = hooks.get(hook.name);
        if (hook0 != null && !(hook0 instanceof FieldHook) && !(hook0 instanceof InvokeHook)) {
            System.err.println("Hook overwrite --> " + id + "#" + hook.name);
        }
        hooks.put(hook.name, hook);
    }

    public final void add(String name, FieldNode fn) {
        if (name == null || fn == null) {
            return;
        }
        Hook hook0 = hooks.get(name);
        if (hook0 != null && !(hook0 instanceof FieldHook) && !(hook0 instanceof InvokeHook)) {
            System.err.println("Hook overwrite --> " + id + "#" + name);
        }
        hooks.put(name, new FieldHook(name, fn));
    }

    public final void add(String name, FieldNode fn, String returnDesc) {
        if (name == null || fn == null) {
            return;
        }
        Hook hook0 = hooks.get(name);
        if (hook0 != null && !(hook0 instanceof FieldHook) && !(hook0 instanceof InvokeHook)) {
            System.err.println("Hook overwrite --> " + id + "#" + name);
        }
        hooks.put(name, new FieldHook(name, fn));
    }

    public final void visit(String visitor, BlockVisitor bv) {
        ClassNode cn = updater.visitor(visitor).cn;
        if (cn == null) {
            return;
        }
        for (FlowGraph graph : updater.graphs().get(cn).values()) {
            this.graph = graph;
            for (Block block : graph) {
                if (bv.validate()) {
                    bv.visit(block);
                }
            }
        }
    }

    public final void visit(BlockVisitor bv) {
        visit(id(), bv);
        bv.visitEnd();
    }

    public final void visitAll(BlockVisitor... bv) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (FlowGraph graph : map.values()) {
                this.graph = graph;
                for (Block block : graph) {
                    for (BlockVisitor b : bv) {
                        if (b.validate())
                            b.visit(block);
                    }
                }
            }
        }
        for (BlockVisitor b : bv)
            b.visitEnd();
    }

    public final void visitIf(BlockVisitor bv, Predicate<Block> blockPredicate) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (FlowGraph graph : map.values()) {
                this.graph = graph;
                for (Block block : graph) {
                    if (bv.validate() && blockPredicate.test(block))
                        bv.visit(block);
                }
            }
        }
        bv.visitEnd();
    }

    public final void visitMethod(MethodNode mn, BlockVisitor bv) {
        for (FlowGraph graph : updater.graphs().get(mn.owner).values()) {
            if (graph.method().name.equalsIgnoreCase(mn.name)) {
                this.graph = graph;
                for (Block block : this.graph) {
                    if (bv.validate()) {
                        bv.visit(block);
                    }
                }
            }
        }
        bv.visitEnd();
    }

    public final void visitMethod(BlockVisitor bv, String owner, String name, String desc) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (Map.Entry<MethodNode, FlowGraph> graph : map.entrySet()) {
                MethodNode mn = graph.getKey();
                if (mn.owner.name.equals(owner) && mn.name.equals(name) && mn.desc.equals(desc)) {
                    this.graph = graph.getValue();
                    for (Block block : this.graph) {
                        if (bv.validate())
                            bv.visit(block);
                    }
                }
            }
        }
        bv.visitEnd();
    }

    public final void visitHookedMethod(BlockVisitor bv, InvokeHook hook) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (Map.Entry<MethodNode, FlowGraph> graph : map.entrySet()) {
                MethodNode mn = graph.getKey();
                if (mn.owner.name.equals(hook.clazz) && mn.name.equals(hook.method) && mn.desc.equals(hook.desc)) {
                    this.graph = graph.getValue();
                    for (Block block : this.graph) {
                        if (bv.validate())
                            bv.visit(block);
                    }
                }
            }
        }
        bv.visitEnd();
    }

    public final void visitMethodIf(BlockVisitor bv, Predicate<MethodNode> methodPredicate) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (Map.Entry<MethodNode, FlowGraph> graph : map.entrySet()) {
                if (!methodPredicate.test(graph.getKey()))
                    continue;
                this.graph = graph.getValue();
                for (Block block : this.graph) {
                    if (bv.validate())
                        bv.visit(block);
                }
            }
        }
        bv.visitEnd();
    }

    public final void visitLocalMethodIf(BlockVisitor bv, Predicate<MethodNode> methodPredicate) {
        for (Map.Entry<MethodNode, FlowGraph> graph : updater.graphs().get(cn).entrySet()) {
            if (!methodPredicate.test(graph.getKey()))
                continue;
            this.graph = graph.getValue();
            for (Block block : this.graph) {
                if (bv.validate())
                    bv.visit(block);
            }
        }
        bv.visitEnd();
    }

    public final void visit(MethodVisitor mv) {
        for (MethodNode mn : cn.methods)
            mn.accept(mv);
    }

    public final void visitAll(MethodVisitor mv) {
        for (ClassNode cn : updater.classnodes.values()) {
            for (MethodNode mn : cn.methods)
                mn.accept(mv);
        }
    }

    public final String getHookKey(String hook) {
        Hook h = hooks.get(hook);
        if (h == null) {
            return null;
        }
        String key = null;
        if (h instanceof InvokeHook) {
            key = ((InvokeHook) h).key();
        } else if (h instanceof FieldHook) {
            key = ((FieldHook) h).key();
        }
        return key;
    }

    public final boolean isValueHooked(String owner, String name) {
        for (Hook h : hooks.values()) {
            if (h instanceof FieldHook) {
                if (((FieldHook) h).clazz.equals(owner) && ((FieldHook) h).field.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public final FieldHook getFieldHook(String hook) {
        Hook h = hooks.get(hook);
        if (h instanceof FieldHook) {
            return (FieldHook) h;
        } else {
            for (Hook hh : hooks.values()) {
                if (hh instanceof FieldHook) {
                    FieldHook fh = (FieldHook) hh;
                    if (hook.equals(fh.clazz + "." + fh.field)) {
                        return fh;
                    }
                }
            }
        }
        return null;
    }

    public final InvokeHook getInvokeHook(String hook) {
        Hook h = hooks.get(hook);
        if (h instanceof InvokeHook) {
            return (InvokeHook) h;
        } else {
            for (Hook hh : hooks.values()) {
                if (hh instanceof InvokeHook) {
                    InvokeHook fh = (InvokeHook) hh;
                    System.out.println(fh.key());
                    if (hook.equals(fh.key())) {
                        return fh;
                    }
                }
            }
        }
        return null;
    }

    public String getPackagePostfix() {
        return "com/jagex/client/";
    }
}
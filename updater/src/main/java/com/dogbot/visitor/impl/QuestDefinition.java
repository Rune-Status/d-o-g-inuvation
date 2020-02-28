package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import com.dogbot.visitor.constraint.OpcodeParsingVisitor;
import org.objectweb.casm.Type;
import org.objectweb.casm.commons.cfg.Block;
import org.objectweb.casm.commons.cfg.BlockVisitor;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.NumberNode;
import org.objectweb.casm.commons.cfg.tree.util.TreeBuilder;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.FieldNode;
import org.objectweb.casm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

@VisitorInfo(hooks = {"name", "name2", "parameters", "isCompleted",
        "points", "varps", "varpBits", "skillRequirements", "type", "pointsRequirement", "icon"})
public class QuestDefinition extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(desc("DefinitionLoader")) == 1 && cn.fieldCount(desc("NodeTable")) == 1
                && cn.fieldCount(String.class) == 2 && cn.fieldCount(int[][].class) == 3;
    }

    @Override
    public void visit() {
        for (FieldNode fn : cn.fields) {
            if ((fn.access & ACC_STATIC) == 0) {
                if (fn.desc.equals("Ljava/lang/String;")) {
                    if (!hooks.containsKey("name")) {
                        addHook(new FieldHook("name", fn));
                    } else if (!hooks.containsKey("name2")) {
                        addHook(new FieldHook("name2", fn));
                    }
                } else if (fn.desc.equals(desc("NodeTable"))) {
                    addHook(new FieldHook("parameters", fn));
                }
            }
        }
        visitLocalMethodIf(new Methods(), m -> m.desc.endsWith("Z"));

        Map<Integer, FieldHook> hooks = new HashMap<>();
        hooks.put(3, createRaw("varps", "[[I"));
        hooks.put(4, createRaw("varpBits", "[[I"));
        hooks.put(6, createRaw("type", "I"));
        hooks.put(9, createRaw("points", "I"));
        hooks.put(14, createRaw("skillRequirements", "[[I"));
        hooks.put(15, createRaw("pointsRequirement", "I"));
        hooks.put(17, createRaw("icon", "I"));

        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("(" + desc("Buffer") + "I")) {
                TreeBuilder.build(mn).accept(new OpcodeParsingVisitor(this, hooks));
            }
        }
    }

    private class Methods extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.opcode() == ICONST_2 && nn.hasParent() && nn.parent().opcode() == IALOAD) {
                        Type[] types = Type.getArgumentTypes(nn.method().desc);
                        if (types.length < 1) {
                            return;
                        }
                        //gross but less effort
                        Type varprov = types[0];
                        if (updater.visitor("VariableProvider") == null) {
                            GraphVisitor[] src = updater.visitors;
                            GraphVisitor[] dest = new GraphVisitor[src.length + 1];
                            System.arraycopy(src, 0, dest, 0, src.length);
                            dest[dest.length - 1] = new GraphVisitor() {

                                {
                                    id = "VariableProvider";
                                }

                                @Override
                                public boolean validate(ClassNode cn) {
                                    return cn.name.equals(varprov.getClassName());
                                }

                                @Override
                                public void visit() {

                                }
                            };
                            dest[dest.length - 1].cn = updater.classnodes.get(varprov.getClassName());
                            updater.visitors = dest;
                        }
                        addHook(new InvokeHook("isCompleted", nn.method(), "(L" + varprov.getClassName() + ";)Z"));
                    }
                }
            });
        }
    }
}

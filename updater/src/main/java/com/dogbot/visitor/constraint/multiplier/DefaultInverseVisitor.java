package com.dogbot.visitor.constraint.multiplier;

import com.dogbot.Updater;
import org.rspeer.api.collections.Multiset;
import org.objectweb.casm.Opcodes;
import org.objectweb.casm.commons.cfg.*;
import org.objectweb.casm.commons.cfg.graph.FlowGraph;
import org.objectweb.casm.commons.cfg.tree.*;
import org.objectweb.casm.commons.cfg.tree.node.*;
import org.objectweb.casm.commons.wrapper.*;
import org.objectweb.casm.tree.*;

import java.math.*;
import java.util.*;

public final class DefaultInverseVisitor extends NodeVisitor implements Opcodes, InverseVisitor {

    private final Map<String, List<BigInteger>> decoders = new HashMap<>();
    private final Map<String, List<BigInteger>> encoders = new HashMap<>();

    private final Updater updater;
    private final Map<String, ClassFactory> classes;

    public DefaultInverseVisitor(Updater updater, Map<String, ClassFactory> classes) {
        this.updater = updater;
        this.classes = classes;
    }

    private void collectValidDecoders(String key, Multiset<BigInteger> multiset, boolean longType) {
        if (!decoders.containsKey(key) || !encoders.containsKey(key)) {
            return;
        }

        for (BigInteger bigIntD : decoders.get(key)) {
            for (BigInteger bigIntE : encoders.get(key)) {
                boolean result = bigIntD.intValue() * bigIntE.intValue() == 1;
                if (longType) {
                    result = bigIntD.longValue() * bigIntE.longValue() == 1;
                }
                if (result) {
                    multiset.add(bigIntD);
                    break;
                }
            }
        }
    }

    public BigInteger getDecoder(String clazz, String field, boolean longType) {
        String key = clazz + "." + field;
        Multiset<BigInteger> multiset = new Multiset<>();
        collectValidDecoders(key, multiset, longType);
        if (!multiset.isEmpty() && multiset.uniqueCount() == 1) {
            return multiset.top();
        } else if (decoders.containsKey(key)) {
            multiset.addAll(decoders.get(key));
            return multiset.top();
        } else if (encoders.containsKey(key)) {
            multiset.addAll(encoders.get(key));
            return new Modulus(multiset.top(), longType ? 64 : 32).compute();
        }

        ClassFactory factory = classes.get(clazz);
        if (factory != null) {
            ClassFactory superFactory = classes.get(factory.superName());
            if (superFactory != null) {
                ClassField superField = superFactory.findField(cf -> cf.name().equals(field));
                if (superField != null) {
                    return getDecoder(superField.owner.name(), superField.name(), longType);
                }
            }
        }
        return null;
    }

    @Override
    public void visitOperation(ArithmeticNode an) {
        if (an.children() != 2) {
            return;
        }

        AbstractNode ctx = an.parent();
        if (ctx != null && (ctx.opcode() == PUTSTATIC || ctx.opcode() == PUTFIELD)) {
            return;
        }

        if (an.opcode() != IMUL && an.opcode() != LMUL) {
            return;
        }

        FieldMemberNode left = (FieldMemberNode) an.first(GETFIELD, GETSTATIC);
        NumberNode right = (NumberNode) an.first(LDC);

        if (left == null || right == null) {
            return;
        }

        if (!left.desc().equals("I") && !left.desc().equals("J")) {
            return;
        }

        processCodec(left, right);
    }

    @Override
    public void visitField(FieldMemberNode fmn) {
        if (!fmn.putting()) {
            return;
        }

        if (!fmn.desc().equals("I") && !fmn.desc().equals("J")) {
            return;
        }

        processSetSelf(fmn);
        processSetFromVar(fmn);
        processSetFromUnknown(fmn);
        processSetFromIncrementOrDecrement(fmn);
    }

    private void processSetSelf(FieldMemberNode fmn) {
        if (fmn.children() == 2 && fmn.opcode() == PUTFIELD) {
            FieldMemberNode tgt = fmn.firstField();
            ArithmeticNode mul = (ArithmeticNode) fmn.first(LMUL, IMUL);
            if (tgt != null && mul != null && tgt.getting() && tgt.key().equals(fmn.key())) {
                NumberNode ldc = (NumberNode) mul.first(LDC);
                if (ldc != null) {
                    processCodec(fmn, ldc);
                }
            }
        }
    }

    private void processSetFromVar(FieldMemberNode fmn) {
        if (fmn.children() == 2 && fmn.opcode() == PUTFIELD) {
            VariableNode tgt = fmn.firstVariable();
            ArithmeticNode mul = (ArithmeticNode) fmn.first(LMUL, IMUL);
            if (tgt != null && mul != null) {
                NumberNode ldc = (NumberNode) mul.first(LDC);
                if (ldc != null) {
                    processCodec(fmn, ldc);
                }
            }
        }
    }

    //usually set from other fields or invokes, this MIGHT need more validation
    private void processSetFromUnknown(FieldMemberNode fmn) {
        if (fmn.children() == 1 && fmn.opcode() == PUTSTATIC) {
            ArithmeticNode mul = (ArithmeticNode) fmn.first(LMUL, IMUL);
            if (mul != null && mul.children() == 2) {
                NumberNode ldc = (NumberNode) mul.first(LDC);
                if (ldc != null) {
                    processCodec(fmn, ldc);
                }
            }
        }
    }

    private void processSetFromIncrementOrDecrement(FieldMemberNode fmn) {
        if (fmn.children() == 1) {
            ArithmeticNode addsub = (ArithmeticNode) fmn.first(ISUB, IADD);
            if (addsub != null) {
                ArithmeticNode mul = (ArithmeticNode) addsub.first(IMUL, LMUL);
                if (mul != null) {
                    NumberNode ldc = (NumberNode) mul.first(LDC);
                    if (ldc != null) {
                        processCodec(fmn, ldc);
                    }
                }
            }
        }
    }

    private void processCodec(FieldMemberNode fmn, NumberNode nn) {
        if (!validate((LdcInsnNode) nn.insn(), fmn.fin())) {
            return;
        }
        BigInteger encoder = BigInteger.valueOf(asLong((LdcInsnNode) nn.insn()));
        Modulus mod = new Modulus(encoder, fmn.desc().equals("J") ? 64 : 32);
        if (mod.validate()) {
            String key = fmn.key();
            Map<String, List<BigInteger>> codec = fmn.getting() ? decoders : encoders;
            if (!codec.containsKey(key)) {
                codec.put(key, new LinkedList<>());
            }
            codec.get(key).add(mod.getQuotient());
        }
    }

    @Override
    public Multiset<BigInteger> encoderSet(String clazz, String field) {
        Multiset<BigInteger> multiset = new Multiset<>();
        multiset.addAll(encoders.get(clazz + "." + field));
        return multiset;
    }

    @Override
    public Multiset<BigInteger> decoderSet(String clazz, String field) {
        Multiset<BigInteger> multiset = new Multiset<>();
        multiset.addAll(decoders.get(clazz + "." + field));
        return multiset;
    }

    @Override
    public void debug() {

    }

    @Override
    public void apply() {
        for (ClassFactory cf : classes.values()) {
            for (ClassMethod cm : cf.methods) {
                FlowGraph graph = updater.getGraph(cm.method);
                if (graph == null) {
                    continue;
                }

                for (Block block : graph) {
                    block.tree().accept(this);
                }
            }
        }
    }

    private static long asLong(LdcInsnNode ldc) {
        if (ldc.cst instanceof Long) {
            return (long) ldc.cst;
        }
        return (int) ldc.cst;
    }

    private static boolean validate(LdcInsnNode lin, FieldInsnNode fin) {
        if (fin.desc.equals("J") && lin.cst instanceof Long) {
            return (long) lin.cst % 2 != 0;
        } else if (fin.desc.equals("I") && lin.cst instanceof Integer) {
            return (int) lin.cst % 2 != 0;
        }
        return false;
    }
}

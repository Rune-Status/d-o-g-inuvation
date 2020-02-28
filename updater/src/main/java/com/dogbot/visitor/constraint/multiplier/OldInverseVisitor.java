package com.dogbot.visitor.constraint.multiplier;

import org.rspeer.api.collections.Multiset;
import org.objectweb.casm.MethodVisitor;
import org.objectweb.casm.Opcodes;
import org.objectweb.casm.commons.util.Assembly;
import org.objectweb.casm.commons.wrapper.ClassFactory;
import org.objectweb.casm.commons.wrapper.ClassField;
import org.objectweb.casm.commons.wrapper.ClassMethod;
import org.objectweb.casm.tree.AbstractInsnNode;
import org.objectweb.casm.tree.FieldInsnNode;
import org.objectweb.casm.tree.LdcInsnNode;
import org.objectweb.casm.tree.VarInsnNode;

import java.math.BigInteger;
import java.util.*;

/**
 * @author Tyler Sedlar
 * @since 3/8/15.
 */
public final class OldInverseVisitor extends MethodVisitor implements Opcodes, InverseVisitor {

    private final Map<String, List<BigInteger>> decoders = new HashMap<>();
    private final Map<String, List<BigInteger>> encoders = new HashMap<>();

    private final Map<String, ClassFactory> classes;

    public OldInverseVisitor(Map<String, ClassFactory> classes) {
        this.classes = classes;
    }

    //TODO might rewrite this.
    //return (bn.mouseIdleTime = bn.mouseIdleTime + 1082596793) * -234497911 - 1;
    //picked 1082596793 as multiplier for this

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

    @Override
    public void visitFieldInsn(FieldInsnNode fin) {
        if (fin.desc.equals("I") || fin.desc.equals("J")) {
            AbstractInsnNode first = fin.next();
            if (first == null || first.opcode() == PUTSTATIC || first.opcode() == PUTFIELD) {
                return;
            }
            AbstractInsnNode second = first.next();
            if (second == null || second.opcode() == PUTSTATIC || second.opcode() == PUTFIELD) {
                return;
            }
            AbstractInsnNode third = second.next();
            if (third == null || third.opcode() == PUTSTATIC || third.opcode() == PUTFIELD || third.opcode() == ILOAD) {
                return;
            }
            AbstractInsnNode fourth = third.next();
            if (fourth != null) {
                fourth = fourth.next();
                if (fourth != null) {
                    fourth = fourth.next();
                    if (fourth != null && fourth.opcode() == ALOAD) {
                        if (((VarInsnNode) fourth).var != 0) {
                            return;
                        }
                    }
                }
            }
            List<LdcInsnNode> ldcs = new ArrayList<>();
            LdcInsnNode ldc = Assembly.next(fin, LDC, 2);
            if (ldc != null && validate(ldc, fin)) {
                ldcs.add(ldc);
            }
            ldc = Assembly.previous(fin, LDC, 2);
            if (ldc != null && validate(ldc, fin)) {
                ldcs.add(ldc);
            }
            for (LdcInsnNode ldcInsn : ldcs) {
                Modulus mod = new Modulus(BigInteger.valueOf(asLong(ldcInsn)), fin.desc.equals("J") ? 64 : 32);
                if (mod.validate()) {
                    String key = fin.owner + "." + fin.name;
                    int opcode = ldcInsn.next().opcode();
                    if (opcode != IMUL && opcode != LMUL) {
                        continue;
                    }

                    boolean getting = fin.opcode() == GETFIELD || fin.opcode() == GETSTATIC;
                    Map<String, List<BigInteger>> map = getting ? decoders : encoders;
                    if (!map.containsKey(key)) {
                        map.put(key, new LinkedList<>());
                    }
                    map.get(key).add(mod.getQuotient());
                }
            }
        }
    }

    public BigInteger getDecoder(String clazz, String field, boolean longType) {
        String key = clazz + "." + field;
        Multiset<BigInteger> multiset = new Multiset<>();
        if (decoders.containsKey(key) && encoders.containsKey(key)) {
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
        if (!multiset.isEmpty() && multiset.uniqueCount() == 1) {
            return multiset.top();
        }
        if (decoders.containsKey(key)) {
            multiset.addAll(decoders.get(key));
            return multiset.top();
        }
        if (encoders.containsKey(key)) {
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
                cm.method.accept(this);
            }
        }
    }

    @Override
    public String toString() {
        return Integer.toString(decoders.size());
    }
}

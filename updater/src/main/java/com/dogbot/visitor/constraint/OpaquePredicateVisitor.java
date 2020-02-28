package com.dogbot.visitor.constraint;

import org.objectweb.casm.MethodVisitor;
import org.objectweb.casm.Type;
import org.objectweb.casm.commons.util.Assembly;
import org.objectweb.casm.tree.*;

import java.util.HashMap;
import java.util.Map;

import static org.objectweb.casm.Opcodes.*;

/**
 * @author Tyler Sedlar
 * @since 2/26/2015
 */
public class OpaquePredicateVisitor extends MethodVisitor {

    private final Map<String, OpaquePredicate> PREDICATES = new HashMap<>();

    private MethodNode mn;
    private Class<?> predType;

    private String key(MethodNode mn) {
        return mn.owner.name + "." + mn.name + mn.desc;
    }

    public void accept(MethodNode mn) {
        Type[] types = Type.getArgumentTypes(mn.desc);
        if (types.length == 0)
            return;
        if (types[types.length - 1] == Type.INT_TYPE) {
            predType = int.class;
        } else if (types[types.length - 1] == Type.BYTE_TYPE) {
            predType = byte.class;
        } else if (types[types.length - 1] == Type.SHORT_TYPE) {
            predType = short.class;
        } else {
            return;
        }
        int loadNum = (mn.access & ACC_STATIC) > 0 ? types.length - 1 : types.length;
        InsnList insns = mn.instructions;
        if (Assembly.first(insns, ain -> ain.opcode() == ILOAD && ((VarInsnNode) ain).var == loadNum) == null) {
            PREDICATES.put(key(mn), new OpaquePredicate(0, predType));
            return;
        }
        this.mn = mn;
        mn.accept(this);
    }

    private int numberFor(AbstractInsnNode ain) {
        if (ain instanceof IntInsnNode) {
            return ((IntInsnNode) ain).operand;
        } else if (ain instanceof LdcInsnNode && ((LdcInsnNode) ain).cst instanceof Integer) {
            return (int) ((LdcInsnNode) ain).cst;
        } else if (ain instanceof InsnNode) {
            if (ain.opcode() >= ICONST_0 && ain.opcode() <= DCONST_1) {
                String opname = Assembly.OPCODES[ain.opcode()];
                return Integer.parseInt(opname.substring(opname.length() - 1));
            } else if (ain.opcode() == NOP) {
                return 0;
            } else if (ain.opcode() == ICONST_M1) {
                return -1;
            }
        }
        return Integer.MAX_VALUE;
    }

    private int validPredicateFor(JumpInsnNode jin, int predicate, boolean flip) {
        switch (jin.opcode()) {
            case IFNE:
            case IF_ICMPNE: {
                return predicate + 1;
            }
            case IFEQ:
            case IF_ICMPEQ: {
                return predicate;
            }
            case IFGE:
            case IF_ICMPGE: {
                return predicate - 1;
            }
            case IFGT:
            case IF_ICMPGT: {
                return predicate - (flip ? 1 : -1);
            }
            case IFLE:
            case IF_ICMPLE: {
                return predicate + 1;
            }
            case IFLT:
            case IF_ICMPLT: {
                return predicate + (flip ? 1 : -1);
            }
            default: {
                return predicate;
            }
        }
    }

    @Override
    public void visitJumpInsn(JumpInsnNode jin) {
        AbstractInsnNode ain = jin.next();
        if (ain != null && (ain.opcode() == RETURN || (ain.opcode() == NEW &&
                ((TypeInsnNode) ain).desc.equals("java/lang/IllegalStateException")))) {
            boolean flip = false;
            AbstractInsnNode arg = jin.previous();
            if (arg == null)
                return;
            AbstractInsnNode load = arg.previous();
            if (load == null)
                return;
            int predicate = numberFor(arg);
            if (predicate == Integer.MAX_VALUE) {
                predicate = numberFor(load);
                flip = true;
            }
            if (predicate == Integer.MAX_VALUE)
                return;
            predicate = validPredicateFor(jin, predicate, flip);
            PREDICATES.put(key(mn), new OpaquePredicate(predicate, predType));
        }
    }

    public OpaquePredicate get(String method) {
        return PREDICATES.get(method);
    }

    public OpaquePredicate get(MethodNode mn) {
        return get(mn.owner.name + "." + mn.name + mn.desc);
    }

    @Override
    public String toString() {
        return Integer.toString(PREDICATES.size());
    }

    public static class OpaquePredicate {

        public final int predicate;
        public final Class<?> predicateType;

        public OpaquePredicate(int predicate, Class<?> predicateType) {
            this.predicate = predicate;
            this.predicateType = predicateType;
        }

    }
}

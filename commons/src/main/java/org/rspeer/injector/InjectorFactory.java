package org.rspeer.injector;

import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.rspeer.injector.CodeAdapter.*;

public final class InjectorFactory {

    private static final String DESC_TRIM = "[?!^\\[L;]";

    private InjectorFactory() {
        throw new IllegalAccessError();
    }

    public static String getRealDesc(Modscript modscript, String desc) {
        Type type = null;
        try {
            type = Type.getType(desc);
        } catch (Exception e) {
            type = Type.getType("L" + desc + ";");
        }

        int dimensions = type.getSort() == Type.ARRAY ? type.getDimensions() : 0;
        String element = desc.replaceAll(DESC_TRIM, "");
        switch (element) {
            case "I":
            case "Z":
            case "J":
            case "B":
            case "S":
            case "C":
            case "F":
            case "D": {
                return desc;
            }
        }

        ClassHook resolved = modscript.resolve(element);
        if (resolved == null) {
            return desc;
        }

        StringBuilder returnDesc = new StringBuilder();
        for (int i = 0; i < dimensions; i++) {
            returnDesc.append("[");
        }
        returnDesc.append("L").append(PROVIDER_PACKAGE);
        returnDesc.append("RS").append(resolved.getDefinedName());
        return returnDesc.append(";").toString();
    }

    public static MethodNode newMethod(String name, String desc) {
        return new MethodNode(ACC_PUBLIC, name, desc, null, null);
    }

    public static void injectOnFieldChangeCallback(Modscript modscript, Map<String, ClassNode> classes, FieldHook hook, boolean relatives) {
        String ownersDefinedName = hook.getParent().getDefinedName();
        for (ClassNode cn : classes.values()) {
            if (cn.name.equals(hook.isStatic() ? "client" : hook.getOwner())) {
                if (hook.getDesc().contains("[")) {
                    String realDesc = getRealDesc(modscript, hook.getDesc());
                    InsnList instructions = new InsnList();
                    instructions.add(callCallbackHandler());
                    instructions.add(new VarInsnNode(ILOAD, hook.isStatic() ? 0 : 1));
                    if (!hook.isStatic()) {
                        instructions.add(new VarInsnNode(ALOAD, 0));
                        instructions.add(new VarInsnNode(ALOAD, 0));
                    }

                    instructions.add(new FieldInsnNode(hook.isStatic() ? GETSTATIC : GETFIELD, hook.getOwner(), hook.getInternalName(), hook.getDesc()));
                    instructions.add(new VarInsnNode(ILOAD, hook.isStatic() ? 0 : 1));
                    instructions.add(new InsnNode(hook.getDesc().contains("[I") ? IALOAD : AALOAD));

                    instructions.add(new VarInsnNode(getLoad(realDesc), hook.isStatic() ? 1 : 2));

                    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, CALLBACK_HANDLER, hook.callbackName(hook.isStatic() ? null : ownersDefinedName), ("(I" + (hook.isStatic() ? "" : "L" + PROVIDER_PACKAGE + "RS" + ownersDefinedName + ";") + realDesc + realDesc + ")V").replace("[", ""), false));
                    cn.methods.add(createCallbackArraySetter(modscript, instructions, hook));
                } else {
                    String realDesc = getRealDesc(modscript, hook.getDesc());
                    InsnList instructions = new InsnList();
                    instructions.add(callCallbackHandler());
                    instructions.add(new VarInsnNode(ALOAD, hook.isStatic() ? 1 : 2));
                    if (!hook.isStatic()) {
                        instructions.add(new VarInsnNode(ALOAD, 0));
                        instructions.add(new VarInsnNode(ALOAD, 0));
                    }
                    instructions.add(new FieldInsnNode(hook.isStatic() ? GETSTATIC : GETFIELD, hook.getOwner(), hook.getInternalName(), hook.getDesc()));
                    instructions.add(multiply(hook, false));
                    instructions.add(new VarInsnNode(getLoad(realDesc), hook.isStatic() ? 0 : 1));
                    instructions.add(multiply(hook, false));
                    instructions.add(new MethodInsnNode(INVOKEVIRTUAL, CALLBACK_HANDLER, hook.callbackName(hook.isStatic() ? null : ownersDefinedName), "(Ljava/lang/String;" + (hook.isStatic() ? "" : "L" + PROVIDER_PACKAGE + "RS" + ownersDefinedName + ";") + realDesc + realDesc + ")Z", false));
                    LabelNode node = new LabelNode();
                    instructions.add(new InsnNode(ICONST_1));
                    instructions.add(new JumpInsnNode(IF_ICMPNE, node));
                    instructions.add(new InsnNode(RETURN));
                    instructions.add(node);
                    cn.methods.add(createCallbackSetter(modscript, instructions, hook));
                }
            }
            for (MethodNode mn : cn.methods) {
                if (mn.name.equals(hook.setterName()) || mn.name.contains("<")) {
                    continue;
                }
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain instanceof FieldInsnNode) {
                        FieldInsnNode fin = (FieldInsnNode) ain;
                        if (hook.matches(fin)) {
                            if (hook.getDesc().contains("[")) {
                                int store = hook.getDesc().contains("[I") ? IASTORE : AASTORE;
                                if ((fin.getOpcode() == GETSTATIC || fin.getOpcode() == GETFIELD)) {
                                    AbstractInsnNode next = ain;
                                    while ((next = next.getNext()) != null)
                                        if (next.getOpcode() == store || next.getOpcode() == IALOAD || next.getOpcode() == AALOAD)
                                            break;
                                    if (next == null || next.getOpcode() != store) continue;
                                    AbstractInsnNode temp;
                                    mn.instructions.set(next, temp =
                                            new MethodInsnNode(
                                                    hook.isStatic() ? INVOKESTATIC : INVOKEVIRTUAL,
                                                    hook.isStatic() ? "client" : hook.getOwner(),
                                                    hook.setterName(),
                                                    "(I" + getRealDesc(modscript, hook.getDesc())
                                                            .replace("[", "") + ")" +
                                                            getRealDesc(modscript, hook.getDesc())
                                                                    .replace("[", ""),
                                                    false
                                            ));
                                    mn.instructions.remove(fin);
                                    if (temp.getPrevious().getOpcode() == DUP_X2)
                                        mn.instructions.remove(temp.getPrevious());
                                }
                            } else {
                                if ((fin.getOpcode() == PUTSTATIC || fin.getOpcode() == PUTFIELD)) {
                                    mn.instructions.insertBefore(fin, new LdcInsnNode(cn.name + "." + mn.name + mn.desc));
                                    mn.instructions.set(fin,
                                            new MethodInsnNode(
                                                    hook.isStatic() ? INVOKESTATIC : INVOKEVIRTUAL,
                                                    hook.isStatic() ? "client" : hook.getOwner(),
                                                    hook.setterName(),
                                                    "(" + getRealDesc(modscript, hook.getDesc()) + "Ljava/lang/String;)V",
                                                    false
                                            )
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static InsnList createCallback(InsnList params, String callbackName, String callbackDesc) {
        InsnList insnList = new InsnList();
        insnList.add(callCallbackHandler());
        insnList.add(params);
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, CALLBACK_HANDLER, callbackName, callbackDesc, false));
        return insnList;
    }

    public static MethodNode createInvoker(Modscript modscript, MethodHook hook, boolean isInterface) {
        InsnList loadList = new InsnList();
        Type expectedDescType = Type.getMethodType(hook.getExpectedDesc());
        Type descType = Type.getMethodType(hook.getDesc());
        StringBuilder builder = new StringBuilder("(");
        int stackIndex = 1;
        if (!hook.isStatic()) {
            loadList.add(new VarInsnNode(ALOAD, 0));
        }
        int idx = 0;
        for (Type type : descType.getArgumentTypes()) {
            if (expectedDescType.getArgumentTypes().length > idx && type.equals(expectedDescType.getArgumentTypes()[idx])) {
                loadList.add(new VarInsnNode(type.getOpcode(ILOAD), stackIndex));
                builder.append(getRealDesc(modscript, type.getDescriptor()));
                idx++;
            }
            switch (type.getClassName()) {
                case "long":
                case "double":
                    stackIndex++;
                default:
                    stackIndex++;
            }
        }
        builder.append(")").append(getRealDesc(modscript, descType.getReturnType().getDescriptor()));

        if (!hook.getDesc().equals(hook.getExpectedDesc())) {
            Type type = descType.getArgumentTypes()[descType.getArgumentTypes().length - 1];
            switch (type.getClassName()) {
                case "byte":
                    loadList.add(new IntInsnNode(BIPUSH, 0));
                    break;
                case "int":
                    loadList.add(new InsnNode(ICONST_0));
                    break;
                case "short":
                    loadList.add(new IntInsnNode(SIPUSH, 0));
                    break;
                default:
                    System.out.println(type.getClassName() + " is not added to switch statement!");
                    break;
            }
        }
        MethodNode node = new MethodNode(ACC_PUBLIC, hook.getDefinedName(), builder.toString(), null, null);
        node.instructions.add(loadList);
        node.instructions.add(new MethodInsnNode(hook.isStatic() ? INVOKESTATIC : isInterface ? INVOKEINTERFACE : INVOKEVIRTUAL, hook.getOwner(), hook.getInternalName(), hook.getDesc(), isInterface));
        node.instructions.add(new InsnNode(descType.getReturnType().getOpcode(IRETURN)));
        return node;
    }

    public static InsnList createCallback(Modscript modscript, MethodHook hook, String ret) {
        InsnList loadList = new InsnList();
        Type expectedDescType = Type.getMethodType(hook.getExpectedDesc());
        Type descType = Type.getMethodType(hook.getDesc());
        StringBuilder builder = new StringBuilder("(");
        int stackIndex = 0;
        loadList.add(callCallbackHandler());
        if (!hook.isStatic()) {
            stackIndex++;
            loadList.add(new VarInsnNode(ALOAD, 0));
            builder.append(getRealDesc(modscript, hook.getOwner()));
        }
        int idx = 0;
        for (Type type : descType.getArgumentTypes()) {
            if (expectedDescType.getArgumentTypes().length > idx && type.equals(expectedDescType.getArgumentTypes()[idx])) {
                loadList.add(new VarInsnNode(type.getOpcode(ILOAD), stackIndex));
                builder.append(getRealDesc(modscript, type.getDescriptor()));
                idx++;
            }
            switch (type.getSort()) {
                case Type.LONG:
                case Type.DOUBLE:
                    stackIndex++;
                default:
                    stackIndex++;
            }
        }
        builder.append(")").append(getRealDesc(modscript, ret != null ? ret : expectedDescType.getReturnType().getDescriptor()));

        loadList.add(new MethodInsnNode(INVOKEVIRTUAL, CALLBACK_HANDLER, hook.getDefinedName(), builder.toString(), false));
        return loadList;
    }

    public static InsnList callCallbackHandler() {
        InsnList instructions = new InsnList();
        instructions.add(new MethodInsnNode(INVOKESTATIC, API_PACKAGE + "Game", "getClient", "()L" + PROVIDER_PACKAGE + "RSClient;", false));
        instructions.add(new MethodInsnNode(INVOKEVIRTUAL, "client", "getCallbackHandler", "()L" + CALLBACK_HANDLER + ";", false));
        return instructions;
    }

    public static InsnList multiply(FieldHook hook, boolean inverse) {
        InsnList instructions = new InsnList();
        if (hook.getMultiplier() != 0) {
            if (hook.getDesc().equals("I")) {
                if (inverse)
                    instructions.add(new LdcInsnNode(BigInteger.valueOf(hook.getMultiplier()).modInverse(BigInteger.ONE.shiftLeft(32)).intValue()));
                else
                    instructions.add(new LdcInsnNode((int) hook.getMultiplier()));
                instructions.add(new InsnNode(IMUL));
            } else if (hook.getDesc().equals("J")) {
                if (inverse)
                    instructions.add(new LdcInsnNode(BigInteger.valueOf(hook.getMultiplier()).modInverse(BigInteger.ONE.shiftLeft(64)).longValue()));
                else
                    instructions.add(new LdcInsnNode(hook.getMultiplier()));
                instructions.add(new InsnNode(LMUL));
            }
        }
        return instructions;
    }

    public static MethodNode createCallbackSetter(Modscript modscript, InsnList list, FieldHook hook) {
        String realDesc = getRealDesc(modscript, hook.getDesc());
        MethodNode node = new MethodNode((hook.isStatic() ? ACC_STATIC : 0) | ACC_PUBLIC, hook.setterName(), "(" + realDesc + "Ljava/lang/String;)V", null, null);
        InsnList instructions = new InsnList();
        instructions.add(list);
        if (!hook.isStatic()) {
            instructions.add(new VarInsnNode(ALOAD, 0));
        }
        instructions.add(new VarInsnNode(getLoad(realDesc), hook.isStatic() ? 0 : 1));
        instructions.add(new FieldInsnNode(hook.isStatic() ? PUTSTATIC : PUTFIELD, hook.getOwner(), hook.getInternalName(), hook.getDesc()));
        instructions.add(new InsnNode(RETURN));

        node.instructions.add(instructions);
        return node;
    }

    public static MethodNode createSetter(Modscript modscript, FieldHook hook) {
        String realDesc = getRealDesc(modscript, hook.getDesc());
        MethodNode node = new MethodNode((hook.isStatic() ? ACC_STATIC : 0) | ACC_PUBLIC, hook.setterName(), "(" + realDesc + ")V", null, null);
        InsnList instructions = new InsnList();

        if (!hook.isStatic()) {
            instructions.add(new VarInsnNode(ALOAD, 0));
        }
        instructions.add(new VarInsnNode(getLoad(realDesc), hook.isStatic() ? 0 : 1));
        instructions.add(multiply(hook, true));

        instructions.add(new FieldInsnNode(hook.isStatic() ? PUTSTATIC : PUTFIELD, hook.getOwner(), hook.getInternalName(), hook.getDesc()));
        instructions.add(new InsnNode(RETURN));
        node.instructions.add(instructions);
        return node;

    }

    public static MethodNode createCallbackArraySetter(Modscript modscript, InsnList list, FieldHook hook) {
        String realDesc = getRealDesc(modscript, hook.getDesc());
        MethodNode node = new MethodNode((hook.isStatic() ? ACC_STATIC : 0) | ACC_PUBLIC, hook.setterName(), "(I" + realDesc.replace("[", "") + ")" + realDesc.replace("[", ""), null, null);
        InsnList instructions = new InsnList();
        instructions.add(list);
        if (!hook.isStatic()) {
            instructions.add(new VarInsnNode(ALOAD, 0));
        }
        instructions.add(new FieldInsnNode(hook.isStatic() ? GETSTATIC : GETFIELD, hook.getOwner(), hook.getInternalName(), hook.getDesc()));
        instructions.add(new VarInsnNode(ILOAD, hook.isStatic() ? 0 : 1));
        instructions.add(new VarInsnNode(getLoad(hook.getDesc()), hook.isStatic() ? 1 : 2));
        instructions.add(new InsnNode(hook.getDesc().contains("[I") ? IASTORE : AASTORE));

        instructions.add(new FieldInsnNode(hook.isStatic() ? GETSTATIC : GETFIELD, hook.getOwner(), hook.getInternalName(), hook.getDesc()));
        instructions.add(new VarInsnNode(ILOAD, hook.isStatic() ? 0 : 1));
        instructions.add(new InsnNode(hook.getDesc().contains("[I") ? IALOAD : AALOAD));

        instructions.add(new InsnNode(getReturn(realDesc.replace("[", ""))));

        node.instructions.add(instructions);
        return node;
    }

    public static int getLoad(String desc) {
        char element = desc.replace("[", "").charAt(0);
        if (desc.contains("[")) return ALOAD;
        switch (element) {
            case 'J':
                return LLOAD;
            case 'I':
            case 'Z':
            case 'B':
            case 'S':
            case 'C':
                return ILOAD;
            case 'F':
                return FLOAD;
            case 'D':
                return DLOAD;
            default: {
                return ALOAD;
            }
        }
    }

    public static MethodNode createGetter(Modscript modscript, FieldHook hook) {
        String realDesc = getRealDesc(modscript, hook.getDesc());
        MethodNode node = new MethodNode(ACC_PUBLIC, hook.getterName(), "()" + realDesc, null, null);
        InsnList instructions = new InsnList();
        if (!hook.isStatic()) {
            instructions.add(new VarInsnNode(ALOAD, 0));
        }
        instructions.add(new FieldInsnNode(hook.isStatic() ? GETSTATIC : GETFIELD, hook.getOwner(), hook.getInternalName(), hook.getDesc()));
        instructions.add(multiply(hook, false));

        instructions.add(new InsnNode(getReturn(hook.getDesc())));

        node.instructions = instructions;
        return node;
    }

    private static int getReturn(String desc) {
        char element = desc.replace("[", "").charAt(0);
        if (desc.contains("[")) return ARETURN;
        switch (element) {
            case 'J':
                return LRETURN;
            case 'I':
            case 'Z':
            case 'B':
            case 'S':
            case 'C':
                return IRETURN;
            case 'F':
                return FRETURN;
            case 'D':
                return DRETURN;
            default: {
                return ARETURN;
            }
        }
    }
}

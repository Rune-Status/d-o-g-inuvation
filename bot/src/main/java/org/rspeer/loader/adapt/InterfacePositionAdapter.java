package org.rspeer.loader.adapt;

import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class InterfacePositionAdapter extends CodeAdapter {

    public InterfacePositionAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassNode node = classes.get(modscript.resolve("InterfaceComponent").getInternalName());
        FieldHook relY = modscript.resolve("InterfaceComponent").getField("relativeY");
        if (node != null) {
            FieldNode parentX = new FieldNode(ACC_PRIVATE, "parentX", "I", null, null);
            FieldNode parentY = new FieldNode(ACC_PRIVATE, "parentY", "I", null, null);

            Collections.addAll(node.fields, parentX, parentY);
            node.methods.add(mkSetter(node.name, parentX));
            node.methods.add(mkGetter(node.name, parentX));
            node.methods.add(mkSetter(node.name, parentY));
            node.methods.add(mkGetter(node.name, parentY));
            for (ClassNode cn : classes.values()) {
                for (MethodNode mn : cn.methods) {
                    if ((mn.access & ACC_STATIC) != 0 && mn.desc.startsWith("([L" + node.name + ";IIIIIII")) {
                        outer:
                        for (AbstractInsnNode ain : mn.instructions.toArray()) {
                            if (ain.getOpcode() != IADD) {
                                continue;
                            }
                            int aload = -1, iload = -1;
                            AbstractInsnNode tmp = ain;
                            inner:
                            for (int i = 0; i < 10 && tmp != null; i++) {
                                if (tmp.getOpcode() == GETFIELD) {
                                    FieldInsnNode k = (FieldInsnNode) tmp;
                                    if (k.owner.equals(node.name) && k.name.equals(relY.getInternalName())) {
                                        AbstractInsnNode chk = checkSurrounding(tmp, 6, ILOAD);
                                        if (chk == null) {
                                            continue outer;
                                        }
                                        iload = ((VarInsnNode) chk).var;
                                        AbstractInsnNode tmp2 = tmp;
                                        for (int j = 0; j < 5 && tmp2 != null; j++) {
                                            if (tmp2.getOpcode() == ALOAD) {
                                                aload = ((VarInsnNode) tmp2).var;
                                                break inner;
                                            }
                                            tmp2 = tmp2.getPrevious();
                                        }
                                    }
                                }
                                tmp = tmp.getPrevious();
                            }
                            if (aload == -1 || iload == -1) {
                                continue;
                            }
                            InsnList setStack = new InsnList();
                            setStack.add(new VarInsnNode(ALOAD, aload));
                            setStack.add(new VarInsnNode(ILOAD, iload - 1));
                            setStack.add(new MethodInsnNode(INVOKEVIRTUAL, node.name, "setParentX", "(I)V", false));
                            setStack.add(new VarInsnNode(ALOAD, aload));
                            setStack.add(new VarInsnNode(ILOAD, iload));
                            setStack.add(new MethodInsnNode(INVOKEVIRTUAL, node.name, "setParentY", "(I)V", false));
                            mn.instructions.insert(ain, setStack);
                        }
                    }
                }
            }
        }
    }

    private AbstractInsnNode checkSurrounding(AbstractInsnNode src, int dist, int op) {
        AbstractInsnNode curr = src;
        for (int i = 0; i < dist && curr != null; i++) {
            if (curr.getOpcode() == op) {
                return curr;
            }
            curr = curr.getNext();
        }
        curr = src;
        for (int i = 0; i < dist && curr != null; i++) {
            if (curr.getOpcode() == op) {
                return curr;
            }
            curr = curr.getPrevious();
        }
        return null;
    }

    private MethodNode mkGetter(String clazz, FieldNode fn) {
        String name = "get" + Character.toUpperCase(fn.name.charAt(0)) + fn.name.substring(1);
        MethodNode mn = new MethodNode(ACC_PUBLIC, name, "()I", null, null);
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new FieldInsnNode(GETFIELD, clazz, fn.name, fn.desc));
        mn.instructions.add(new InsnNode(IRETURN));
        mn.visitMaxs(1, 1);
        mn.visitEnd();
        return mn;
    }

    private MethodNode mkSetter(String clazz, FieldNode fn) {
        String name = "set" + Character.toUpperCase(fn.name.charAt(0)) + fn.name.substring(1);
        MethodNode mn = new MethodNode(ACC_PUBLIC, name, "(I)V", null, null);
        mn.instructions.add(new VarInsnNode(ALOAD, 0));
        mn.instructions.add(new VarInsnNode(ILOAD, 1));
        mn.instructions.add(new FieldInsnNode(PUTFIELD, clazz, fn.name, fn.desc));
        mn.instructions.add(new InsnNode(RETURN));
        mn.visitMaxs(2, 2);
        mn.visitEnd();
        return mn;
    }

    @Override
    public String verbose() {
        return "Injected interface container positions!";
    }
}

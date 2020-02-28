package org.rspeer.loader.adapt;

import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.ConstantHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.Map;

public final class AddConstantAdapter extends CodeAdapter {

    public AddConstantAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        InsnList list = new InsnList();
        for (ClassHook hook : modscript.classes.values()) {
            for (Map.Entry<String, ConstantHook> entry : hook.getConstants().entrySet()) {
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new LdcInsnNode(entry.getValue().getDefinedName()));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false));
                LabelNode labelNode = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, labelNode));
                list.add(new LdcInsnNode(entry.getValue().getValue()));
                list.add(new InsnNode(IRETURN));
                list.add(labelNode);
            }
        }
        list.add(new InsnNode(ICONST_M1));
        list.add(new InsnNode(IRETURN));

        MethodNode methodNode = new MethodNode(ACC_PUBLIC, "getConstant", "(Ljava/lang/String;)I", null, null);
        methodNode.instructions.add(list);
        classes.get("client").methods.add(methodNode);
    }

    @Override
    public String verbose() {
        return "Injected constant hooks!";
    }
}

package org.rspeer.loader.adapt;

import org.rspeer.game.providers.RSClient;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Map;

public final class ExceptionSuppressorAdapter extends CodeAdapter {

    private boolean injected = false;

    public ExceptionSuppressorAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                if (!mn.desc.startsWith("(Ljava/lang/String;Ljava/lang/Throwable;") || (mn.access & ACC_STATIC) <= 0) {
                    continue;
                }
                InsnList stack = new InsnList();

                Label label = new Label();
                LabelNode ln = new LabelNode(label);
                mn.visitLabel(label);
                stack.add(new InsnNode(ICONST_0));
                stack.add(new VarInsnNode(ALOAD, 1));
                stack.add(new MethodInsnNode(INVOKESTATIC, RSClient.class.getName().replace('.', '/'),
                        "processException", "(Ljava/lang/Throwable;)Z", true));
                stack.add(new JumpInsnNode(IFNE, ln));
                stack.add(new VarInsnNode(ALOAD, 1));
                stack.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Throwable", "printStackTrace", "()V", false));
                stack.add(new InsnNode(RETURN));
                stack.add(ln);

                mn.instructions.insertBefore(mn.instructions.getFirst(), stack);
                injected = true;
            }
        }
    }

    @Override
    public String verbose() {
        if (injected) {
            return "Injected stack trace verbosity!";
        }
        return "Failed to inject stack trace verbosity!";
    }
}

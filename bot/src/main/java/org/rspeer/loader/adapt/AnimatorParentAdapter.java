package org.rspeer.loader.adapt;

import org.rspeer.game.providers.RSAnimator;
import org.rspeer.game.providers.RSMobile;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Map;

public final class AnimatorParentAdapter extends CodeAdapter {

    public AnimatorParentAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassHook mobileHook = modscript.resolve(RSMobile.class);
        ClassHook animatorHook = modscript.resolve(RSAnimator.class);

        FieldHook animHook = mobileHook.getField("animator");

        ClassNode cn = classes.get(mobileHook.getInternalName());
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("<init>")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == RETURN) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, cn.name, animHook.getInternalName(), animHook.getDesc()));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, animatorHook.getInternalName(), "setOwner", "(L" + RSMobile.class.getName().replace('.', '/') + ";)V", false));
                        mn.instructions.insertBefore(ain, list);
                    }
                }
            }
        }
    }

    @Override
    public String verbose() {
        return "Added owner to Animator!";
    }
}

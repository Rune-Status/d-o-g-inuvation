package org.rspeer.loader.adapt;

import org.objectweb.asm.tree.*;
import org.rspeer.game.providers.RSCombatGauge;
import org.rspeer.game.providers.RSMobile;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.Modscript;

import java.util.Map;

public final class CombatGaugeParentAdapter extends CodeAdapter {

    private int count = 0;

    public CombatGaugeParentAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassHook mobileHook = modscript.resolve(RSMobile.class);
        ClassHook gaugeHook = modscript.resolve(RSCombatGauge.class);

        ClassNode cn = classes.get(mobileHook.getInternalName());
        for (MethodNode mn : cn.methods) {
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getOpcode() == INVOKESPECIAL && ain.getNext() != null) {
                    MethodInsnNode min = (MethodInsnNode) ain;
                    if (ain.getNext().getOpcode() != ASTORE
                            || !min.owner.equals(gaugeHook.getInternalName())
                            || !min.name.equals("<init>")) {
                        continue;
                    }

                    InsnList stack = new InsnList();
                    stack.add(new VarInsnNode(ALOAD, ((VarInsnNode) ain.getNext()).var));
                    stack.add(new VarInsnNode(ALOAD, 0));
                    stack.add(new MethodInsnNode(INVOKEVIRTUAL, gaugeHook.getInternalName(), "setOwner", "(L" + RSMobile.class.getName().replace('.', '/') + ";)V", false));
                    mn.instructions.insert(ain.getNext(), stack);

                    count++;
                }
            }
        }
    }

    @Override
    public String verbose() {
        return "Added " + count + " CombatGauge parent setters!";
    }
}

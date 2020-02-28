package org.rspeer.loader.adapt;

import org.rspeer.game.providers.RSBuffer;
import org.rspeer.game.providers.RSClient;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Map;

public final class RandomDatAdapter extends CodeAdapter {

    public RandomDatAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        String buffer = modscript.resolve(RSBuffer.class).getInternalName();
        MethodHook anim = modscript.resolve(RSClient.class).getMethod("getRandomDat");

        for (MethodNode mn : classes.get(anim.getOwner()).methods) {
            if (mn.name.equals(anim.getInternalName()) && mn.desc.equals(anim.getDesc())) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain instanceof MethodInsnNode) {
                        MethodInsnNode min = (MethodInsnNode) ain;
                        if (min.owner.equals(buffer) && min.desc.matches("\\(\\[BII(I|B|S|)\\)V")) {
                            AbstractInsnNode prev = ain;
                            while ((prev = prev.getPrevious()) != null && prev.getOpcode() != Opcodes.ALOAD) ;
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, ((VarInsnNode) prev).var));
                            mn.instructions.insert(prev,
                                    InjectorFactory.createCallback(list, "getRandomDat", "([B)[B"));
                            mn.instructions.remove(prev);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String verbose() {
        return "Injected random.dat hack!";
    }
}

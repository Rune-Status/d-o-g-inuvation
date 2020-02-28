package org.rspeer.loader.adapt;

import org.rspeer.game.api.Game;
import org.rspeer.game.providers.RSRenderConfiguration;
import org.rspeer.game.providers.RSTestHook;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Map;

public final class EntityHoverCallback extends CodeAdapter {

    public EntityHoverCallback(Modscript modscript) {
        super(modscript);
    }

    public void test() {
        Game.getClient().getCallbackHandler().entityHovered(null, 69, 69);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassHook cfg = modscript.resolve(RSRenderConfiguration.class);
        ClassNode cn = classes.get(modscript.resolve(RSTestHook.class).getInternalName());
        for (MethodNode mn : cn.methods) {
            if ((mn.access & ACC_STATIC) == 0 && mn.desc.startsWith("(L" + cfg.getInternalName() + ";II")) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == IRETURN) {
                        AbstractInsnNode prev = ain.getPrevious();
                        if (prev.getOpcode() == ICONST_1) {
                            InsnList stack = new InsnList();
                            stack.add(new VarInsnNode(ALOAD, 0));
                            stack.add(new VarInsnNode(ILOAD, 2));
                            stack.add(new VarInsnNode(ILOAD, 3));
                            mn.instructions.insertBefore(prev,
                                    InjectorFactory.createCallback(stack, "entityHovered",
                                            "(L" + PROVIDER_PACKAGE + "RSTestHook;II)V")
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public String verbose() {
        return "Injected entity hover callback!";
    }
}

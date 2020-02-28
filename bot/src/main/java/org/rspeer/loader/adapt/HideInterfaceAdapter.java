package org.rspeer.loader.adapt;

import org.rspeer.game.providers.RSClient;
import org.rspeer.game.providers.RSInterfaceComponent;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Map;

public final class HideInterfaceAdapter extends CodeAdapter {

    public HideInterfaceAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        MethodHook fh = modscript.resolve(RSClient.class).getMethod("shouldRenderComponent");
        ClassHook ch = modscript.resolve(RSInterfaceComponent.class);
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain instanceof MethodInsnNode) {
                        MethodInsnNode min = (MethodInsnNode) ain;
                        if (min.owner.equals(fh.getOwner()) && min.name.equals(fh.getInternalName()) && min.desc.equals(fh.getDesc())) {
                            mn.instructions.insertBefore(ain.getPrevious(), InjectorFactory.callCallbackHandler());
                            mn.instructions.insertBefore(ain, new MethodInsnNode(INVOKEVIRTUAL, CALLBACK_HANDLER, "shouldRenderComponent", "(L" + PROVIDER_PACKAGE + "RS" + ch.getDefinedName() + ";)Z", false));
                            mn.instructions.remove(ain);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String verbose() {
        return "Injected InterfaceComponent.hidden bypass!";
    }
}

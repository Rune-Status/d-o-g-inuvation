package org.rspeer.loader.adapt;

import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.Map;

public final class EngineTickCallback extends CodeAdapter {

    public EngineTickCallback(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassNode cn = classes.get("client");
        for (MethodNode mn : cn.methods) {
            if (mn.desc.length() <= 4 && mn.desc.endsWith("V") && (mn.access & ACC_STATIC) == 0) {
                xd:
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain.getOpcode() == SIPUSH) {
                        IntInsnNode iin = (IntInsnNode) ain;
                        if (iin.operand == 1000) {
                            try {
                                AbstractInsnNode tmp = ain.getPrevious().getPrevious().getPrevious();
                                for (int i = 0; i < 6 && tmp != null; i++) {
                                    if (tmp.getOpcode() == IREM) {
                                        mn.instructions.insertBefore(mn.instructions.getFirst(), InjectorFactory.createCallback(new InsnList(), "onEngineTick", "()V"));
                                        break xd;
                                    }
                                    tmp = tmp.getNext();
                                }
                            } catch (Exception ignore) {

                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public String verbose() {
        return "Injected engine tick callback!";
    }
}

package org.rspeer.loader.adapt;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.rspeer.game.providers.RSFont;
import org.rspeer.game.providers.RSPureJavaRenderConfiguration;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.Modscript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class JavaFontAdapter extends CodeAdapter {

    private int count = 0;

    public JavaFontAdapter(Modscript modscript) {
        super(modscript);
    }

    private static <I> List<I> collect(I input, Function<I, I> transformer, int repeat) {
        if (repeat <= 0) {
            throw new IllegalArgumentException("repeat must be > 0");
        }

        List<I> output = new ArrayList<>();
        for (int i = 0; i < repeat; i++) {
            if (input == null) {
                return Collections.emptyList();
            }
            output.add(input);
            input = transformer.apply(input);
        }
        return output;
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassHook rendererHook = modscript.resolve(RSPureJavaRenderConfiguration.class);
        ClassHook fontHook = modscript.resolve(RSFont.class);
        ClassNode renderer = classes.get(rendererHook.getInternalName());
        if (renderer == null || fontHook == null) {
            System.out.println("WARNING: Failed to patch RS font loader due to broken hooks");
            return;
        }

        for (MethodNode mn : renderer.methods) {
            if (!mn.desc.endsWith("L" + fontHook.getInternalName() + ";") && (mn.access & ACC_STATIC) > 0) {
                continue;
            }

            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getOpcode() != ATHROW) {
                    continue;
                }

                List<AbstractInsnNode> sequence = collect(ain, AbstractInsnNode::getPrevious, 6);
                Collections.reverse(sequence);
                if (sequence.isEmpty()) {
                    continue;
                }

                AbstractInsnNode stmt = sequence.get(0);
                if (stmt.getOpcode() != IFEQ) {
                    continue;
                }

                AbstractInsnNode value = stmt.getPrevious();
                if (value.getOpcode() != ILOAD) {
                    continue;
                }

                mn.instructions.set(value, new InsnNode(ICONST_0)); //change from if (bool) to if (false)
                count++;
            }
        }
    }

    @Override
    public String verbose() {
        return "Patched " + count + " occurences of RS font loader bug";
    }
}

package org.rspeer.loader.adapt;

import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * CodeAdapter that is responsible for simple method callbacks. Just adds a callback to the first instruction.
 */
public final class AddCallbackAdapter extends CodeAdapter {

    private final MethodHook hook;
    private final boolean escapeOnTrue;

    public AddCallbackAdapter(Modscript modscript, MethodHook hook, boolean escapeOnTrue) {
        super(modscript);
        this.hook = hook;
        this.escapeOnTrue = escapeOnTrue;
    }

    public AddCallbackAdapter(Modscript modscript, MethodHook hook) {
        this(modscript, hook, false);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassNode owner = classes.get(hook.getOwner());
        for (MethodNode mn : owner.methods) {
            if (hook.matches(mn)) {
                InsnList list = InjectorFactory.createCallback(modscript, hook, escapeOnTrue ? "Z" : null);
                if (escapeOnTrue) {
                    LabelNode node = new LabelNode();
                    list.add(new JumpInsnNode(IFEQ, node));
                    list.add(new InsnNode(RETURN));
                    list.add(node);
                }
                mn.instructions.insertBefore(mn.instructions.getFirst(), list);
            }
        }
    }

    @Override
    public String verbose() {
        return String.format("Injected callback for %s!", hook.getParent().getDefinedName() + "." + hook.getDefinedName());
    }
}

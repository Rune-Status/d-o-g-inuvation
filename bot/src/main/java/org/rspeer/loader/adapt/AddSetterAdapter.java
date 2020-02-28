package org.rspeer.loader.adapt;

import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public final class AddSetterAdapter extends CodeAdapter {

    private FieldHook hook;

    public AddSetterAdapter(Modscript modscript, FieldHook hook) {
        super(modscript);
        this.hook = hook;
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassNode node = classes.get(hook.isStatic() ? "client" : hook.getOwner());

        node.methods.add(InjectorFactory.createSetter(modscript, hook));

    }

    @Override
    public String verbose() {
        return String.format("Added setter for %s", hook.getParent().getDefinedName() + "." + hook.getDefinedName());
    }
}

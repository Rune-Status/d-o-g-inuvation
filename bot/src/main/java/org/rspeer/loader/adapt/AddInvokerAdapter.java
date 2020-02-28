package org.rspeer.loader.adapt;

import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.MethodHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public final class AddInvokerAdapter extends CodeAdapter {

    private int count = 0;

    public AddInvokerAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        for (ClassHook ch : modscript.classes.values()) {
            for (MethodHook mh : ch.getMethods().values()) {
                if (mh.getExpectedDesc().equals("omit")) {
                    continue;
                }

                ClassNode owner = classes.get(mh.getOwner());
                ClassNode location = !mh.isStatic() ? owner : classes.get(modscript.getClient().getInternalName());
                boolean itf = (owner.access & ACC_INTERFACE) > 0;
                location.methods.add(InjectorFactory.createInvoker(modscript, mh, itf));
                count++;
            }
        }
    }

    @Override
    public String verbose() {
        return String.format("Injected %d method invokers!", count);
    }
}

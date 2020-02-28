package org.rspeer.injector;

import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public abstract class CodeAdapter implements Opcodes {

    public static final String PROVIDER_PACKAGE = "org/rspeer/game/providers/";
    public static final String CALLBACK_HANDLER = "org/rspeer/game/event/callback/ClientCallbackHandler";
    public static final String API_PACKAGE = "org/rspeer/game/api/";

    protected final Modscript modscript;

    public CodeAdapter(Modscript modscript) {
        this.modscript = modscript;
    }

    public abstract void transform(Map<String, ClassNode> classes);

    public abstract String verbose();

    public final void visit(Map<String, ClassNode> classes) {
        transform(classes);
        System.out.println(verbose());
    }
}

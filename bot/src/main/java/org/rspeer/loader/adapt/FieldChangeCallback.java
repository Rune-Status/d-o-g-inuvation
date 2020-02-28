package org.rspeer.loader.adapt;

import org.rspeer.game.providers.*;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.FieldHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public final class FieldChangeCallback extends CodeAdapter {

    private int count = 0;

    public FieldChangeCallback(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        FieldHook[] hooks = {
                modscript.resolve(RSClient.class).getField("connectionState"),
                modscript.resolve(RSClient.class).getField("loginResponse"),
                modscript.resolve(RSClient.class).getField("lobbyResponse"),
                modscript.resolve(RSInterfaceComponent.class).getField("renderCycle"),
                modscript.resolve(RSBufferedConnection.class).getField("idleTicks"),
                modscript.resolve(RSAnimator.class).getField("animation"),
                modscript.resolve(RSSkill.class).getField("experience"),
                modscript.resolve(RSSkill.class).getField("currentLevel"),
                modscript.resolve(RSSkill.class).getField("level")
        };

        for (FieldHook hook : hooks) {
            InjectorFactory.injectOnFieldChangeCallback(modscript, classes, hook, false);
        }
        count = hooks.length;
    }

    @Override
    public String verbose() {
        return String.format("Injected %d field change callbacks!", count);
    }
}

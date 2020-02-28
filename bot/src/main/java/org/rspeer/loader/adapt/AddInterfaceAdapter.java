package org.rspeer.loader.adapt;

import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.ClassNode;

import java.util.Map;

public final class AddInterfaceAdapter extends CodeAdapter {

    private int count = 0;

    public AddInterfaceAdapter(Modscript modscript) {
        super(modscript);
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
//        modscript.classes.put("OpenGL", new ClassHook("OpenGL", "jaggl/OpenGL"));//TODO
//        modscript.classes.put("Direct3D", new ClassHook("Direct3D", "jagdx/IDirect3D"));//TODO
//        modscript.classes.put("Direct3DSurface", new ClassHook("Direct3DSurface", "jagdx/IDirect3DSurface"));//TODO
//        modscript.classes.put("Direct3DDevice", new ClassHook("Direct3DDevice", "jagdx/IDirect3DDevice"));//TODO
//        modscript.classes.put("Direct3DSwapChain", new ClassHook("Direct3DSwapChain", "jagdx/IDirect3DSwapChain"));//TODO
        for (ClassHook hook : modscript.classes.values()) {
            ClassNode node = classes.get(hook.getInternalName());
            if (node != null) {
                node.interfaces.add(PROVIDER_PACKAGE + "RS" + hook.getDefinedName());
                count++;
            }
        }
    }

    @Override
    public String verbose() {
        return String.format("Injected %d interfaces!", count);
    }
}

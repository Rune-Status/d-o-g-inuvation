package org.rspeer.injector.hook;

import org.rspeer.io.Crypto;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ClassHook extends Hook {

    private final Map<String, FieldHook> fields;
    private final Map<String, MethodHook> methods;
    private final Map<String, ConstantHook> constants;

    private String name, internalName;

    public ClassHook(String name, String internalName) {
        this();
        this.name = name;
        this.internalName = internalName;
    }

    public ClassHook() {
        fields = new HashMap<>();
        methods = new HashMap<>();
        constants = new HashMap<>();
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        internalName = Crypto.decrypt(in.readUTF());
        name = Crypto.decrypt(in.readUTF());

        int hookCount = in.readInt();
        for (int j = 0; j < hookCount; j++) {
            Hook hook = Hook.readFrom(in);
            if (hook == null) {
                continue;
            }
            if (hook instanceof FieldHook) {
                FieldHook fh = (FieldHook) hook;
                fh.setParent(this);
                fields.put(fh.getDefinedName(), fh);
            } else if (hook instanceof MethodHook) {
                MethodHook ih = (MethodHook) hook;
                ih.setParent(this);
                methods.put(ih.getDefinedName(), ih);
            } else if (hook instanceof ConstantHook) {
                ConstantHook ch = (ConstantHook) hook;
                constants.put(ch.getDefinedName(), ch);
            }
        }
    }

    @Override
    public String getDefinedName() {
        return name;
    }

    public String getInternalName() {
        return internalName;
    }

    public FieldHook getField(String definedName) {
        return fields.get(definedName);
    }

    public MethodHook getMethod(String definedName) {
        return methods.get(definedName);
    }

    public ConstantHook getConstant(String definedName) {
        return constants.get(definedName);
    }

    public Map<String, FieldHook> getFields() {
        return fields;
    }

    public Map<String, MethodHook> getMethods() {
        return methods;
    }

    public Map<String, ConstantHook> getConstants() {
        return constants;
    }
}

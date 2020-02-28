package org.rspeer.loader.adapt;

import jdk.internal.org.objectweb.asm.Type;
import org.rspeer.injector.CodeAdapter;
import org.rspeer.injector.InjectorFactory;
import org.rspeer.injector.hook.ClassHook;
import org.rspeer.injector.hook.Modscript;
import org.objectweb.asm.tree.*;

import java.util.Map;

/**
 * Inserts a field along with a getter and setter into the target class
 */
public final class AddDataFieldAdapter extends CodeAdapter {

    private final ClassHook target;
    private final String fieldName, fieldDesc;

    public AddDataFieldAdapter(Modscript modscript, ClassHook target, String fieldName, String fieldDesc) {
        super(modscript);
        this.target = target;
        this.fieldName = fieldName;
        this.fieldDesc = fieldDesc;
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        ClassNode node = classes.get(target.getInternalName());
        node.fields.add(new FieldNode(ACC_PRIVATE, fieldName, fieldDesc, null, null));

        MethodNode getter = InjectorFactory.newMethod(prefix("get"), "()" + fieldDesc);
        getter.instructions.add(new VarInsnNode(ALOAD, 0));
        getter.instructions.add(new FieldInsnNode(GETFIELD, target.getInternalName(), fieldName, fieldDesc));
        getter.instructions.add(new InsnNode(Type.getReturnType("()" + fieldDesc).getOpcode(IRETURN)));

        MethodNode setter = InjectorFactory.newMethod(prefix("set"), "(" + fieldDesc + ")V");
        setter.instructions.add(new VarInsnNode(ALOAD, 0));
        setter.instructions.add(new VarInsnNode(Type.getReturnType("()" + fieldDesc).getOpcode(ILOAD), 1));
        setter.instructions.add(new FieldInsnNode(PUTFIELD, target.getInternalName(), fieldName, fieldDesc));
        setter.instructions.add(new InsnNode(RETURN));

        node.methods.add(getter);
        node.methods.add(setter);
    }

    private String prefix(String prefix) {
        return prefix + fieldName.toUpperCase().charAt(0) + fieldName.substring(1);
    }

    @Override
    public String verbose() {
        return "Inserted field " + target.getDefinedName() + "." + fieldName;
    }
}

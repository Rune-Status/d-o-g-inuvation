package org.objectweb.casm.commons.cfg.transform;

import org.objectweb.casm.MethodVisitor;
import org.objectweb.casm.commons.wrapper.ClassFactory;
import org.objectweb.casm.commons.wrapper.ClassMethod;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodInsnNode;

import java.util.*;

/**
 * @author Tyler Sedlar
 * @since 3/19/15.
 */
public class UnusedMethodTransform extends Transform {

    private final Map<String, ClassFactory> factories;
    private final List<ClassMethod> entryPoints = new LinkedList<>();
    private final Set<String> validMethodKeys = new HashSet<>();
    private final List<ClassMethod> validMethods = new LinkedList<>();

    private int totalMethods = 0;

    public UnusedMethodTransform(Map<String, ClassFactory> factories) {
        this.factories = factories;
    }

    public void populateEntryPoints(List<ClassMethod> entries) {
        for (ClassFactory factory : factories.values()) {
            entries.addAll(factory.findMethods(cm -> cm.method.name.length() > 3));
            entries.addAll(factory.findMethods(cm -> {
                String superName = factory.node.superName;
                while (superName != null) {
                    if (factories.containsKey(superName) && factories.get(superName).findMethod(icm ->
                            icm.method.name.equals(cm.method.name) && icm.method.desc.equals(cm.method.desc)) != null) {
                        return true;
                    }
                    ClassFactory super_ = factories.get(superName);
                    superName = super_ != null ? super_.superName() : null;
                }
                return false;
            }));
            entries.addAll(factory.findMethods(cm -> {
                for (String iface : factory.node.interfaces) {
                    if (factories.containsKey(iface)) {
                        ClassFactory impl = factories.get(iface);
                        while (impl != null) {
                            ClassFactory temp = search(impl, cm);
                            if (temp != null && temp == impl) {
                                return true;
                            }
                            impl = temp;
                        }
                    }
                }
                return false;
            }));
        }
    }

    private ClassFactory search(ClassFactory impl, ClassMethod cm) {
        if (impl.findMethod(icm -> icm.method.name.equals(cm.method.name) &&
                icm.method.desc.equals(cm.method.desc)) != null) {
            return impl;
        }
        if (impl.interfaces() != null && impl.interfaces().size() > 0) {
            for (String superimpl : impl.interfaces()) {
                ClassFactory superimpll = factories.get(superimpl);
                if (superimpll != null) {
                    if (superimpll.findMethod(icm -> icm.method.name.equals(cm.method.name) &&
                            icm.method.desc.equals(cm.method.desc)) != null) {
                        return impl;
                    } else {
                        return search(superimpll, cm);
                    }
                }
            }
        }
        return null;
    }

    public List<ClassMethod> found() {
        return validMethods;
    }

    private void follow(Map<String, ClassFactory> classes, List<ClassMethod> followed, ClassMethod method) {
        if (validMethodKeys.contains(method.key())) {
            return;
        }
        validMethodKeys.add(method.key());
        followed.add(method);
        method.method.accept(new MethodVisitor() {
            public void visitMethodInsn(MethodInsnNode min) {
                if (classes.containsKey(min.owner)) {
                    ClassFactory factory = classes.get(min.owner);
                    ClassMethod innerMethod = factory.findMethod(cm -> cm.method.name.equals(min.name) &&
                            cm.method.desc.equals(min.desc));
                    if (innerMethod != null)
                        follow(classes, followed, innerMethod);
                    ClassFactory super_ = factories.get(factory.superName());
                    while (super_ != null) {
                        ClassMethod method = super_.findMethod(stm -> stm.method.name.equals(min.name) &&
                                stm.method.desc.equals(min.desc));
                        if (method != null)
                            follow(classes, followed, method);
                        super_ = factories.get(super_.superName());
                    }
                    for (String iface : factory.interfaces()) {
                        ClassFactory impl = factories.get(iface);
                        while (impl != null) {
                            ClassMethod method = impl.findMethod(im -> im.method.name.equals(min.name) &&
                                    im.method.desc.equals(min.desc));
                            if (method != null)
                                follow(classes, followed, method);
                            impl = factories.get(impl.superName());
                        }
                    }
                }
            }
        });
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        Map<String, ClassFactory> factories = new HashMap<>();
        for (ClassNode cn : classes.values())
            factories.put(cn.name, new ClassFactory(cn));
        populateEntryPoints(entryPoints);
        for (ClassNode cn : classes.values())
            totalMethods += cn.methods.size();
        for (ClassMethod method : entryPoints)
            follow(factories, validMethods, method);
        for (ClassFactory cf : factories.values()) {
            cf.findMethods(mf -> !validMethodKeys.contains(mf.key())).forEach(ClassMethod::remove);
        }
    }

    @Override
    public String toString() {
        int removed = totalMethods - validMethods.size();
        return "Removed " + removed + "/" + totalMethods + " methods [Kept " + validMethods.size() + "]";
    }

    public int getRemoved() {
        return totalMethods - validMethods.size();
    }

    public int getValid() {
        return validMethods.size();
    }

    public int getTotal() {
        return totalMethods;
    }
}

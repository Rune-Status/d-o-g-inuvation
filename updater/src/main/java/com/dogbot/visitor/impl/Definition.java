package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 07-08-2015
 */
public class Definition extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        if ((cn.access & ACC_INTERFACE) == 0) {
            return false;
        }
        ClassNode anim = updater.classnodes.get(clazz("Animation"));
        if (anim != null && anim.interfaces != null && anim.interfaces.size() == 1) {
            String iface = anim.interfaces.get(0);
            return cn.name.equals(iface);
        }
        return false;
    }

    @Override
    public void visit() {

    }
}

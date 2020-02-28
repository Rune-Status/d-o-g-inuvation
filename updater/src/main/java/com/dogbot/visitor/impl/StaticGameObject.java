package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by Inspiron on 08/12/2016.
 */
@VisitorInfo(hooks = {"config"})
public class StaticGameObject extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.superName.equals(clazz("SceneEntity"))
                && cn.implement(clazz("SceneObject"))
                && cn.fieldCount(desc("ObjectConfig")) == 1
                && cn.abnormalFieldCount() == 2
                && cn.fieldCount(boolean.class) == 3;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("config", cn.getField(null, desc("ObjectConfig"))));
    }
}

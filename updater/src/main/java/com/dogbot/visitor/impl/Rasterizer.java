package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

/**
 * Created by mdawg on 10/17/2017.
 */
public class Rasterizer extends GraphVisitor {
    @Override
    public boolean validate(ClassNode cn) {
        return cn.methodCount(e -> e.desc.startsWith("(ZZZFFFFFFFFFFFF")) > 0;//boolean var1, boolean var2, boolean var3, float var4, float var5, float var6, float var7,
//        float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15
    }

    @Override
    public void visit() {

    }
}

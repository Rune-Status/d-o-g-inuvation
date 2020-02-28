/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.FieldHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 01-08-2015
 */
@VisitorInfo(hooks = {"randomAccessFile"})
public class FileOnDisk extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount("J") == 2 && cn.fieldCount("Ljava/io/RandomAccessFile;") == 1;
    }

    @Override
    public void visit() {
        addHook(new FieldHook("randomAccessFile", cn.getField(null, "Ljava/io/RandomAccessFile;")));
    }
}

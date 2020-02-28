/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.impl;

import com.dogbot.hookspec.hook.InvokeHook;
import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

/**
 * @author Dogerina
 * @since 12-08-2015
 */
@VisitorInfo(hooks = {"destroy", "init", "paint", "update", "supplyApplet", "start", "stop"})
public class Loader extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.getMethodByName("supplyApplet") != null && (cn.access & ACC_INTERFACE) != 0;
    }

    @Override
    public void visit() {
        for (String hook : new String[]{"destroy", "init", "paint", "update", "supplyApplet", "start", "stop"}) {
            addHook(new InvokeHook(hook, cn.getMethodByName(hook), descFor(hook)));
        }
    }

    private String descFor(String hook) {
        switch (hook) {
            case "init":
            case "start":
            case "stop":
            case "destroy": {
                return "()V";
            }
            case "paint":
            case "update": {
                return "(Ljava/awt/Graphics;)V";
            }
            case "supplyApplet": {
                return "(Ljava/applet/Applet;)V";
            }
            default: {
                throw new IllegalArgumentException("???");
            }
        }
    }

}

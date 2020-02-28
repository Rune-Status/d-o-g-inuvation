package com.dogbot.visitor.impl;

import com.dogbot.visitor.GraphVisitor;
import com.dogbot.visitor.VisitorInfo;
import org.objectweb.casm.tree.ClassNode;

import java.net.InetAddress;

/**
 * Created by Inspiron on 08/12/2016.
 */
@VisitorInfo(hooks = {"host", "address"})
public class LatencyMonitor extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.fieldCount(InetAddress.class) == 1 && cn.fieldCount(String.class) == 1;
    }

    @Override
    public void visit() {
        add("host", cn.getField(null, "Ljava/lang/String;"));
        add("address", cn.getField(null, "Ljava/net/InetAddress;"));
        //add("latency", cn.getField(null, "J"));
        //add("running", cn.getField(null, "Z"));
    }
}

/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.dogbot.visitor.constraint;

import com.dogbot.Updater;
import org.objectweb.casm.commons.cfg.tree.NodeVisitor;
import org.objectweb.casm.commons.cfg.tree.node.AbstractNode;
import org.objectweb.casm.commons.cfg.tree.node.ConstantNode;
import org.objectweb.casm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.casm.commons.cfg.tree.node.MethodMemberNode;
import org.objectweb.casm.tree.ClassNode;
import org.objectweb.casm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Dogerina
 * @since 09-08-2015
 */
public class TranslatableStringVisitor {

    private final Map<String, Value> englishToValues;
    private final AtomicReference<ClassNode> classNodeRef;

    public TranslatableStringVisitor(Updater updater) {
        this.englishToValues = new HashMap<>();
        this.classNodeRef = new AtomicReference<>();
        for (ClassNode cn : updater.classnodes.values()) {
            if (cn.fieldCount("L" + cn.name + ";", false) > 150) {
                if (cn.getMethod("<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V") != null) {
                    classNodeRef.set(cn);
                    break;
                }
            }
        }
        if (classNodeRef.get() == null) {
            throw new RuntimeException("failed to find TranslatableString class");
        }
        MethodNode clinit = classNodeRef.get().getMethodByName("<clinit>");
        if (clinit == null) {
            throw new RuntimeException("wrong class?");
        }
        updater.graphs().get(clinit.owner).get(clinit).forEach(block -> block.tree().accept(new NodeVisitor() {
            @Override
            public void visitMethod(MethodMemberNode mmn) {
                if (mmn.name().equals("<init>") && mmn.hasParent() && mmn.parent().opcode() == PUTSTATIC) {
                    if (mmn.desc().equals("(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")) {
                        List<AbstractNode> layers = mmn.layerAll(LDC);
                        if (layers != null) {
                            List<String> strings = new ArrayList<>();
                            layers.forEach(l -> strings.add((String) ((ConstantNode) l).cst()));
                            if (strings.size() != 5) {
                                return;
                            }
                            String[] asArray = new String[5];
                            strings.toArray(asArray);
                            englishToValues.put(asArray[0], new Value((FieldMemberNode) mmn.parent(), asArray));
                        }
                    }
                }
            }
        }));
    }

    public Value valueFor(String english) {
        return englishToValues.get(english);
    }

    public String germanFor(String english) {
        Value value = valueFor(english);
        if (value == null) {
            return null;
        }
        return value.getGerman();
    }

    public String frenchFor(String english) {
        Value value = valueFor(english);
        if (value == null) {
            return null;
        }
        return value.getFrench();
    }

    public String portugueseFor(String english) {
        Value value = valueFor(english);
        if (value == null) {
            return null;
        }
        return value.getPortuguese();
    }

    public String latinSpanishFor(String english) {
        Value value = valueFor(english);
        if (value == null) {
            return null;
        }
        return value.getLatinSpanish();
    }

    public String englishFor(FieldMemberNode fmn) {
        for (Map.Entry<String, Value> entry : englishToValues.entrySet()) {
            if (entry.getValue().getFieldMemberNode().key().equals(fmn.key())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public FieldMemberNode fieldFor(String english) {
        Value value = valueFor(english);
        if (value == null) {
            return null;
        }
        return value.getFieldMemberNode();
    }

    public AtomicReference<ClassNode> getClassNodeRef() {
        return classNodeRef;
    }

    public ClassNode getClassNode() {
        return classNodeRef.get();
    }

    public class Value {

        private final FieldMemberNode fieldMemberNode;
        private final String english, german, french, portuguese, latinSpanish;

        private Value(FieldMemberNode fieldMemberNode, String english, String german, String french,
                      String portuguese, String latinSpanish) {
            this.fieldMemberNode = fieldMemberNode;
            this.english = english;
            this.german = german;
            this.french = french;
            this.portuguese = portuguese;
            this.latinSpanish = latinSpanish;
        }

        private Value(FieldMemberNode fieldMemberNode, String... strings) {
            if (strings.length != 5) {
                throw new IllegalArgumentException("bad_strings");
            }
            this.fieldMemberNode = fieldMemberNode;
            this.english = strings[0];
            this.german = strings[1];
            this.french = strings[2];
            this.portuguese = strings[3];
            this.latinSpanish = strings[4];
        }

        public FieldMemberNode getFieldMemberNode() {
            return fieldMemberNode;
        }

        public String getEnglish() {
            return english;
        }

        public String getGerman() {
            return german;
        }

        public String getFrench() {
            return french;
        }

        public String getPortuguese() {
            return portuguese;
        }

        public String getLatinSpanish() {
            return latinSpanish;
        }
    }
}

package org.rspeer.injector;

import org.rspeer.Configuration;
import org.rspeer.io.InnerPack;
import org.rspeer.injector.hook.Modscript;
import org.rspeer.loader.Crawler;
import org.rspeer.loader.GameConfiguration;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.*;

public final class Injector implements AutoCloseable {

    private final Modscript modscript;
    private final InnerPack innerPack;

    private final List<CodeAdapter> adapters;

    private Injector(Modscript modscript, InnerPack innerPack) {
        this.modscript = modscript;
        this.innerPack = innerPack;
        adapters = new ArrayList<>();
    }

    public static Injector decrypting(InnerPack innerPack, GameConfiguration configuration, byte[] bytes) throws Exception {
        Modscript modscript = new Modscript();
        Crawler crawler = new Crawler(configuration);
        modscript.load(bytes == null ? Files.readAllBytes(Paths.get(Configuration.CACHE + crawler.getRemoteHash() + ".dat")) : bytes);
        return new Injector(modscript, innerPack);
    }

    private static Map<String, ClassNode> decode(Map<String, byte[]> data) {
        Map<String, ClassNode> classes = new HashMap<>();
        for (Map.Entry<String, byte[]> entry : data.entrySet()) {
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(entry.getValue());
            reader.accept(node, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
            classes.put(node.name, node);
        }
        return classes;
    }

    private static Map<String, byte[]> encode(Map<String, ClassNode> data) {
        Map<String, byte[]> classes = new HashMap<>();
        for (Map.Entry<String, ClassNode> entry : data.entrySet()) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            entry.getValue().accept(writer);
            classes.put(entry.getKey().replace('/', '.'), writer.toByteArray());
        }
        return classes;
    }

    public Map<String, byte[]> getBuffer() throws GeneralSecurityException, IOException {
        Map<String, ClassNode> inject = decode(innerPack.decrypt());
        adapters.forEach(x -> x.visit(inject));
        return encode(inject);
    }

    @Override
    public void close() throws Exception {
        innerPack.close();
    }

    public void accept(CodeAdapter... adapters) {
        Collections.addAll(this.adapters, adapters);
    }

    public Modscript getModscript() {
        return modscript;
    }

}

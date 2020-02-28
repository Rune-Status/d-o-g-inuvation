package org.objectweb.casm.commons.util;

import org.objectweb.casm.ClassReader;
import org.objectweb.casm.ClassWriter;
import org.objectweb.casm.tree.ClassNode;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * @author Tyler Sedlar
 */
public class JarArchive {

    private final Map<String, ClassNode> nodes = new HashMap<>();
    private final Map<String, byte[]> resources = new HashMap<>();

    private final JarInputStream inputStream;
    private Manifest manifest;

    public JarArchive(JarInputStream inputStream) {
        this.inputStream = inputStream;
    }

    private byte[] inputToBytes(InputStream in) {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public Map<String, ClassNode> build() {
        if (!nodes.isEmpty())
            return nodes;
        try {
            JarInputStream jar = inputStream;
            manifest = jar.getManifest();
            ZipEntry entry;
            while ((entry = jar.getNextEntry()) != null) {
                String name = entry.getName();
                if (name.endsWith(".class")) {
                    ClassNode cn = new ClassNode();
                    ClassReader reader = new ClassReader(inputToBytes(jar));
                    reader.accept(cn, ClassReader.EXPAND_FRAMES);
                    nodes.put(name.replace(".class", "").replace("/", "."), cn);
                } else {
                    if (!name.equals("META-INF/MANIFEST.MF"))
                        resources.put(name, inputToBytes(jar));
                }
            }
            jar.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error building classes: ", e.getCause());
        }
        return nodes;
    }

    public void write(File target) {
        try (JarOutputStream output = (manifest != null ? new JarOutputStream(new FileOutputStream(target), manifest) :
                new JarOutputStream(new FileOutputStream(target)))) {
            for (Map.Entry<String, ClassNode> entry : build().entrySet()) {
                output.putNextEntry(new JarEntry(entry.getKey().replaceAll("\\.", "/") + ".class"));
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                entry.getValue().accept(writer);
                output.write(writer.toByteArray());
                output.closeEntry();
            }
            for (Map.Entry<String, byte[]> entry : resources.entrySet()) {
                output.putNextEntry(new JarEntry(entry.getKey()));
                output.write(entry.getValue());
                output.closeEntry();
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

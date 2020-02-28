package org.rspeer.io;

import java.io.*;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

public final class Archive {

    public static final int BUFFER_SIZE = 5 * 1024 * 1024;

    private static byte[] read(InputStream input) throws IOException {
        int read;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (input.available() > 0 && (read = input.read(buffer, 0, buffer.length)) != -1) {
                output.write(buffer, 0, read);
            }
            return output.toByteArray();
        }
    }

    public static void write(Map<String, byte[]> def, OutputStream dest) {
        try (JarOutputStream output = new JarOutputStream(dest)) {
            for (Map.Entry<String, byte[]> entry : def.entrySet()) {
                output.putNextEntry(new JarEntry(entry.getKey().replaceAll("\\.", "/") + ".class"));
                output.write(entry.getValue());
                output.closeEntry();
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write(Map<String, byte[]> def, File dest) {
        try {
            write(def, new FileOutputStream(dest));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

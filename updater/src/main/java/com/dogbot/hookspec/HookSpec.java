package com.dogbot.hookspec;

import com.dogbot.hookspec.hook.Hook;
import com.dogbot.visitor.GraphVisitor;
import org.objectweb.casm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * @author Tyler Sedlar, Dogerina
 */
public class HookSpec {

    private static final int MAGIC = "yeet".getBytes().length;

    public static void write(String file, String hash, Collection<GraphVisitor> visitors) throws Exception {
        Files.write(Paths.get(file), write(hash, visitors));
    }

    public static byte[] write(String hash, Collection<GraphVisitor> visitors) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream out = new DataOutputStream(baos)) {
            out.writeInt(MAGIC);
            out.writeUTF(hash);
            out.writeInt(visitors.size());
            for (GraphVisitor gv : visitors) {
                ClassNode cn = gv.cn;
                out.writeBoolean(cn != null);
                if (cn == null) {
                    continue;
                }
                out.writeUTF(Crypto.encrypt(cn.name));
                out.writeUTF(Crypto.encrypt(gv.id()));
                out.writeInt(gv.hooks.size());
                for (Hook hook : gv.hooks.values()) {
                    hook.writeToEncryptedStream(out);
                }
            }
            out.flush();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR WRITING MODSCRIPT");
        }
        return baos.toByteArray();
    }
}

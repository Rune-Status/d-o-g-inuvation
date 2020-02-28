package org.rspeer.io;

import org.rspeer.loader.GameConfiguration;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

/**
 * Decrypts the {@code inner.pack.gz} archive in the {@code gamepack} jar file.
 *
 * @author Major
 */
public final class InnerPack implements Closeable {

    private static final byte[] EMPTY_KEY = new byte[0];

    private static final String ENCRYPTED_ARCHIVE_NAME = "inner.pack.gz";
    private static final String SECRET_PARAMETER_NAME = "0";
    private static final String VECTOR_PARAMETER_NAME = "-1";

    private final String encodedSecret;
    private final String encodedVector;

    private final InputStream input;

    private String sha1 = null;

    private InnerPack(String archive, String encodedSecret, String encodedVector) throws IOException {
        this.encodedSecret = encodedSecret;
        this.encodedVector = encodedVector;
        ByteArrayInOutStream byteArrayInOutStream = new ByteArrayInOutStream();
        try (JarInputStream jis = new JarInputStream(new URL(archive).openStream())) {
            ZipEntry entry;
            while ((entry = jis.getNextEntry()) != null) {
                if (entry.getName().equals(ENCRYPTED_ARCHIVE_NAME)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = jis.read(buffer)) != -1) {
                        byteArrayInOutStream.write(buffer, 0, length);
                    }
                    break;
                }
            }
        }
        try {
            MessageDigest hash = MessageDigest.getInstance("SHA-1");
            byte[] array = hash.digest(byteArrayInOutStream.toByteArray());
            sha1 = toHexString(array);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        input = new BufferedInputStream(byteArrayInOutStream.getInputStream());
    }

    public String sha1() {
        return sha1;
    }

    private static String toHexString(byte[] array) {
        StringBuilder builder = new StringBuilder(array.length * 2);
        for (byte b : array) {
            builder.append((b & 0xff) + 0x100);
            //builder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return builder.toString();
    }

    private InnerPack(GameConfiguration configuration) throws IOException {
        this(configuration.getArchive(), configuration.getParameter(SECRET_PARAMETER_NAME),
                configuration.getParameter(VECTOR_PARAMETER_NAME));
    }

    public static InnerPack open(String archive, String encodedSecret, String encodedVector) throws IOException {
        return new InnerPack(archive, encodedSecret, encodedVector);
    }

    public static InnerPack open(GameConfiguration configuration) throws IOException {
        return new InnerPack(configuration);
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    private final Map<String, byte[]> decrypted = new HashMap<>();

    public Map<String, byte[]> decrypt() throws GeneralSecurityException, IOException {
        if (!decrypted.isEmpty()) {
            return decrypted;
        }
        byte[] secretKey = (encodedSecret.length() == 0) ? EMPTY_KEY : decodeBase64(encodedSecret);
        byte[] initialisationVector = (encodedVector.length() == 0) ? EMPTY_KEY : decodeBase64(encodedVector);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secret = new SecretKeySpec(secretKey, "AES");
        IvParameterSpec vector = new IvParameterSpec(initialisationVector);

        cipher.init(Cipher.DECRYPT_MODE, secret, vector);

        byte[] buffer = new byte[Archive.BUFFER_SIZE];
        int read = 0, in = 0;

        while (read < buffer.length && (in = input.read(buffer, read, buffer.length - read)) != -1) {
            read += in;
        }

        byte[] decrypted = cipher.doFinal(buffer, 0, read);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(Archive.BUFFER_SIZE);

        try (JarOutputStream jar = new JarOutputStream(bos);
             GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(decrypted))) {
            Pack200.newUnpacker().unpack(gzip, jar);
        }

        Map<String, byte[]> classes = new HashMap<>();

        try (JarInputStream jar = new JarInputStream(new ByteArrayInputStream(bos.toByteArray()))) {
            for (JarEntry entry = jar.getNextJarEntry(); entry != null; entry = jar.getNextJarEntry()) {
                String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }

                read = 0;
                while (read < buffer.length && (in = jar.read(buffer, read, buffer.length - read)) != -1) {
                    read += in;
                }

                // Don't change the casting, fixes java 8 issues where some ByteBuffer methods dont exist for some reason.
                // https://stackoverflow.com/questions/48693695/java-nio-buffer-not-loading-clear-method-on-runtime
                ByteBuffer data = ByteBuffer.allocate(read);
                data = data.put(buffer, 0, read);
                Buffer cast = data;
                cast.limit(cast.position())
                        .mark()
                        .position(0);
                data = (ByteBuffer) cast;
                classes.put(name.replace(".class", "").replace("/", "."), data.array());
            }
        }
        this.decrypted.putAll(classes);
        return classes;
    }

    private byte[] decodeBase64(String string) {
        String valid = string.replace('*', '+').replace('-', '/');
        Base64.Decoder base64 = Base64.getDecoder();
        return base64.decode(valid);
    }
}
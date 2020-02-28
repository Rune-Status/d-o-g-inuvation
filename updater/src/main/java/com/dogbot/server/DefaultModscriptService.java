package com.dogbot.server;

import com.dogbot.RS3Updater;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.rspeer.io.Archive;
import org.rspeer.io.ByteArrayInOutStream;
import org.rspeer.io.InnerPack;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.jar.JarInputStream;

public class DefaultModscriptService implements ModscriptService {

    private static final Gson g = new Gson();

    public byte[] process(String json) {
        JsonObject object = g.fromJson(json, JsonObject.class);
        String sha1 = object.getAsJsonPrimitive("sha1").getAsString();
        String archive = object.getAsJsonPrimitive("archive").getAsString();
        String secret = object.getAsJsonPrimitive("secret").getAsString();
        String vector = object.getAsJsonPrimitive("vector").getAsString();
        try (InnerPack innerPack = InnerPack.open(archive, secret, vector)) {
            ByteArrayInOutStream stream = new ByteArrayInOutStream();
            Archive.write(innerPack.decrypt(), stream);
            return process(sha1, stream.toByteArray());
        } catch (IOException | GeneralSecurityException e) {
            return null;
        }
    }

    private byte[] process(String hash, byte[] buffer) {
        RS3Updater updater = updater(hash, buffer);
        if (updater == null) {
            return null;
        }
        updater.setPrint(false);
        updater.run();
        byte[] modscript = updater.modscript(false);
        updater.flush();
        return modscript;
    }

    private RS3Updater updater(String hash, byte[] buffer) {
        try {
            ByteArrayInOutStream stream = new ByteArrayInOutStream();
            stream.write(buffer);
            return new RS3Updater(new JarInputStream(stream.getInputStream()), hash);
        } catch (Exception e) {
            return null;
        }
    }

}

package org.rspeer.ui.account;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.rspeer.api.commons.Crypto;
import org.rspeer.api.commons.GameAccount;
import org.rspeer.rspeer_rest_api.RSPeerApi;
import org.rspeer.script.Script;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;

public final class XorSerializedAccountList extends ArrayList<GameAccount> {

    private static final Type ACCOUNT_LIST_TYPE = new TypeToken<ArrayList<GameAccount>>() {
    }.getType();
    private static final String ACCOUNT_FILE_NAME = "abtash.dat";

    public XorSerializedAccountList() {
        try {
            deserialize();
        } catch (IOException e) {
            System.err.println("Error deserializing account data");
        }
    }

    void serialize() {
        File file = getAccountFile();
        Gson gson = new Gson();
        String json = gson.toJson(this, ACCOUNT_LIST_TYPE);
        byte[] encrypted = Crypto.xor(json.getBytes(), String.valueOf(getUserId()));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(encrypted);
        } catch (IOException exception) {
            exception.printStackTrace();
            System.err.println("Error writing account data");
        }
    }

    private void deserialize() throws IOException {
        File file = getAccountFile();
        if (!Files.exists(file.toPath())) {
            return;
        }
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            data = Crypto.xor(data, String.valueOf(getUserId()));
            String json = new String(data);
            Gson gson = new Gson();
            ArrayList<GameAccount> list = gson.fromJson(json, ACCOUNT_LIST_TYPE);
            for (Object element : list) {
                add((GameAccount) element);
            }
        } catch (JsonSyntaxException exception) {
            //todo add this back ?
        }
    }

    private File getAccountFile() {
        return new File(Script.getDataDirectory().toString(), ACCOUNT_FILE_NAME);
    }

    private int getUserId() {
        return RSPeerApi.getUserId();
    }
}
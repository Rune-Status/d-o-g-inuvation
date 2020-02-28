package org.rspeer.io;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public final class HttpCommons {

    private static final Gson gson = new Gson();

    private HttpCommons() {
        throw new IllegalAccessError();
    }

    public static String streamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String getIpAddress() {
        try {
            URLConnection connection = new URL("https://api.ipify.org?format=json").openConnection();
            InputStream input = connection.getInputStream();
            JsonObject element = gson.fromJson(new InputStreamReader(input), JsonObject.class);
            input.close();
            return element.get("ip").getAsString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static JSONObject streamToJson(InputStream is) {
        String res = streamToString(is);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(res);
    }
}


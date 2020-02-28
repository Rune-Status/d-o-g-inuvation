package org.rspeer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Configuration {

    public static final String APPLICATION_NAME = "RSPeer Inuvation";

    public static final String HOME = getSystemHome() + File.separator + APPLICATION_NAME + File.separator;
    public static final String CACHE = HOME + "cache" + File.separator;
    public static final String DATA = CACHE + "data" + File.separator;
    public static final String SCRIPTS = HOME + "scripts" + File.separator;
    public static final String PICTURES = HOME + "pictures" + File.separator;
    public static final String LOGS = HOME + "logs" + File.separator;

    public static final String ME = CACHE + "misc";
    public static final String ME_NEW = CACHE + "misc_new";
    public static final String ME_OLD = CACHE + "rspeer_me";

    public static final String MAIN_JAR = CACHE + "inuvation.jar";

    public static final String[] DIRECTORIES = {CACHE, DATA, SCRIPTS, PICTURES, LOGS};

    public static final String API_BASE = System.getenv("api_url") != null
            ? System.getenv("api_url")
            : "https://services.rspeer.org/api/";

    public static final String CLIENT_TAG = "rspeer_client_rs3";

    static {
        for (String dir : DIRECTORIES) {
            new File(dir).mkdirs();
        }
    }

    private Configuration() {
        throw new IllegalAccessError();
    }

    public static Path resolvePath(String child) {
        return Paths.get(HOME).resolve(child);
    }

    public static String getSystemHome() {
        return OperatingSystem.get() == OperatingSystem.WINDOWS ? System.getProperty("user.home")
                + File.separator + "Documents" + File.separator : System.getProperty("user.home") + File.separator;
    }
}

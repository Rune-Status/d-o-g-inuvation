package org.rspeer;

import java.io.IOException;
import java.util.Arrays;

public class Bootstrap {

    private static final String flags = "-noverify -Xmx1g";

    private static String location() {
        return Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ");
    }

    private static boolean valid() {
        return Double.parseDouble(System.getProperty("java.version").substring(0, 3)) >= 1.8;
    }

    public static void main(String... arguments) throws IOException {
        if (!valid()) {
            return;
        }
        String command = buildCommand(arguments);
        if (System.getProperty("os.name").contains("Windows")) {
            Runtime.getRuntime().exec(command);
        } else {
            Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
        }
    }

    private static String buildCommand(String... arguments) {

        StringBuilder builder = new StringBuilder();
        String system = System.getProperty("os.name");
        if (system.contains("Windows") || system.contains("Linux") || system.contains("Mac")) {
            builder.append("java " + flags);
        }
        builder.append(" -cp \"").append(location()).append("\" ").append(Inuvation.class.getName());
        builder.append(" ").append(Arrays.toString(arguments).replace("[", "").replace("]", "").replace(",", ""));
        return builder.toString();
    }
}
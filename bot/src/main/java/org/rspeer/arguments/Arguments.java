package org.rspeer.arguments;

import org.apache.commons.cli.*;
import org.rspeer.Inuvation;
import org.rspeer.api.commons.GameAccount;
import org.rspeer.game.api.Game;

import java.io.File;
import java.lang.management.ManagementFactory;

public final class Arguments {

    private String serverIp;
    private String proxy;
    private String account;
    private String script;

    private String[] scriptArgs;

    private String qsPath;
    private QuickStartArgs qs;

    public Arguments(String... args) {
        Options options = new Options();

        options.addOption(Option
                .builder("server")
                .hasArg()
                .required(false)
                .argName("server")
                .desc("server ip | DEPRECATED")
                .build());

        options.addOption(Option
                .builder("proxy")
                .hasArg()
                .required(false)
                .argName("proxy")
                .desc("proxy ip | DEPRECATED")
                .build());

        options.addOption(Option
                .builder("account")
                .hasArg()
                .required(false)
                .argName("account")
                .desc("-account user:pass | DEPRECATED")
                .build());

        options.addOption(Option
                .builder("script")
                .hasArg()
                .required(false)
                .argName("script")
                .desc("script name - surround with \"\" if multi word arg | DEPRECATED")
                .build());

        options.addOption(Option
                .builder("scriptargs")
                .hasArg()
                .required(false)
                .argName("scriptargs")
                .desc("script args, separate with comma. Surround with \"\" if multi word arg | DEPRECATED")
                .build());

        options.addOption(Option
                .builder("qs")
                .hasArg()
                .required(false)
                .argName("quickstart")
                .desc("qs quickstart.json")
                .build());

        options.addOption(Option
                .builder("quickstart")
                .hasArg()
                .required(false)
                .argName("quickstart")
                .desc("qs quickstart.json")
                .build());

        CommandLine parser = null;
        try {
            parser = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        serverIp = parser.getOptionValue("server");
        proxy = parser.getOptionValue("proxy");
        account = parser.getOptionValue("account");
        script = parser.getOptionValue("script");
        scriptArgs = parser.getOptionValues("scriptargs");
        qsPath = parser.getOptionValue("qs");
        if (qsPath == null || qsPath.trim().isEmpty()) {
            qsPath = parser.getOptionValue("quickstart");
        }

        if (account != null) {
            Game.setDefaultAccount(new GameAccount(account));
        }

        if (qsPath != null) {
            try {
                qs = QuickStartArgs.parse(qsPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (qs != null && qs.isUseProxy()) {
            this.proxy = qs.getProxyIp() + ":" + qs.getProxyPort() + ":" + qs.getProxyUsername() + ":" + qs.getProxyPass();
        }
    }

    private String constructCommand(String... args) {
        StringBuilder cmd = new StringBuilder();
        cmd.append("\"").append(System.getProperty("java.home"))
                .append(File.separator).append("bin")
                .append(File.separator).append("java\" ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (jvmArg.startsWith("-")) {
                cmd.append(jvmArg);
            } else {
                cmd.append("\"").append(jvmArg).append("\"");
            }
            cmd.append(" ");
        }
        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
        cmd.append(Inuvation.class.getName()).append(" ");
        for (String arg : args) {
            cmd.append(arg).append(" ");
        }
        return cmd.toString();
    }

    public String getServerIp() {
        return serverIp;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public QuickStartArgs getQuickLaunch() {
        return qs;
    }

    public String getScript() {
        return script;
    }

    public String[] getScriptArgs() {
        return scriptArgs;
    }
}

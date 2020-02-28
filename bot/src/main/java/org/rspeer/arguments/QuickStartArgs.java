package org.rspeer.arguments;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@DoNotRename
public final class QuickStartArgs {

    @Expose
    @SerializedName(value = "RsUsername", alternate = {"rsUsername"})
    @DoNotRename
    private String rsUsername;

    @Expose
    @SerializedName(value = "RsPassword", alternate = {"rsPassword"})
    @DoNotRename
    private String rsPassword;

    @Expose
    @SerializedName(value = "World", alternate = "world")
    @DoNotRename
    private int world;

    @Expose
    @SerializedName(value = "ScriptName", alternate = "scriptName")
    @DoNotRename
    private String scriptName;

    @Expose
    @SerializedName(value = "IsRepoScript", alternate = "isRepoScript")
    @DoNotRename
    private boolean isRepoScript;

    @Expose
    @SerializedName(value = "ScriptArgs", alternate = "scriptArgs")
    @DoNotRename
    private String scriptArgs;

    @Expose
    @SerializedName(value = "Config", alternate = "config")
    @DoNotRename
    private Config config;

    @Expose
    @SerializedName(value = "BreakProfile", alternate = "breakProfile")
    @DoNotRename
    private String breakProfile;

    @SerializedName(value = "UseProxy", alternate = "useProxy")
    @DoNotRename
    private boolean useProxy;

    @SerializedName(value = "ProxyIp", alternate = "proxyIp")
    @DoNotRename
    private String proxyIp;

    @SerializedName(value = "ProxyUser", alternate = "proxyUser")
    @DoNotRename
    private String proxyUsername;

    @SerializedName(value = "ProxyPass", alternate = "proxyPass")
    @DoNotRename
    private String proxyPass;

    @SerializedName(value = "ProxyPort", alternate = "proxyPort")
    @DoNotRename
    private int proxyPort;

    @DoNotRename
    public static QuickStartArgs parse(String value) throws Exception {
        List<Exception> errors = new ArrayList<>();
        try {
            String json = new String(Base64.getDecoder().decode(value));
            return new Gson().fromJson(json, QuickStartArgs.class);
        } catch (Exception e) {
            errors.add(e);
        }
        try {
            URL url = new URL(value);
            if (url.getHost() != null) {
                HttpResponse<String> res = Unirest.get(value).asString();
                return new Gson().fromJson(res.getBody(), QuickStartArgs.class);
            }
        } catch (Exception e) {
            errors.add(e);
        }
        if (Files.exists(Paths.get(value))) {
            try {
                String text = new String(Files.readAllBytes(Paths.get(value)));
                return new Gson().fromJson(text, QuickStartArgs.class);
            } catch (IOException e) {
                errors.add(e);
            }
        } else {
            errors.add(new Exception("Configuration file does not exist at path: " + value + ". Unable to process quick launch args. Make sure to provide the absolute path to a file."));
        }
        System.err.println("Exhausted all options to parse provided quick launch configuration.");
        StringBuilder stack = new StringBuilder();
        for (Exception error : errors) {
            stack.append(error.toString());
        }
        throw new Exception(stack.toString());
    }

    @DoNotRename
    public String getRsUsername() {
        return rsUsername;
    }

    public void setRsUsername(String rsUsername) {
        this.rsUsername = rsUsername;
    }

    @DoNotRename
    public String getRsPassword() {
        return rsPassword;
    }

    public void setRsPassword(String rsPassword) {
        this.rsPassword = rsPassword;
    }

    @DoNotRename
    public int getWorld() {
        return world;
    }

    public void setWorld(int world) {
        this.world = world;
    }

    @DoNotRename
    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    @DoNotRename
    public boolean getIsRepoScript() {
        return isRepoScript;
    }

    @DoNotRename
    public String getScriptArgs() {
        return scriptArgs;
    }

    public void setScriptArgs(String scriptArgs) {
        this.scriptArgs = scriptArgs;
    }

    @DoNotRename
    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @DoNotRename
    public String getBreakProfile() {
        return breakProfile;
    }

    public void setBreakProfile(String breakProfile) {
        this.breakProfile = breakProfile;
    }

    @DoNotRename
    public boolean getRepoScript() {
        return isRepoScript;
    }

    public void setRepoScript(boolean repoScript) {
        isRepoScript = repoScript;
    }

    @DoNotRename
    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    @DoNotRename
    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    @DoNotRename
    public String getProxyUsername() {
        return proxyUsername;
    }

    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    @DoNotRename
    public String getProxyPass() {
        return proxyPass;
    }

    public void setProxyPass(String proxyPass) {
        this.proxyPass = proxyPass;
    }

    @DoNotRename
    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @DoNotRename
    public class Config {

        @Expose
        @SerializedName(value = "LowCpuMode", alternate = "lowCpuMode")
        @DoNotRename
        private boolean lowCpuMode;

        @DoNotRename
        public boolean getLowCpuMode() {
            return lowCpuMode;
        }
    }
}

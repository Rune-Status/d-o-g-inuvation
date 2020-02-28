package org.rspeer.loader;

import org.rspeer.io.JavConfig;

import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

public final class GameEnvironment implements GameConfiguration {

    private static final String ARCHIVE_KEY = "initial_jar";

    private static final GameSource SOURCE = GameSource.RUNESCAPE;
    private static final Language LANGUAGE = Language.ENGLISH;

    private final Map<String, String> parameters = new HashMap<>();

    private final ProxyConfig proxy;

    public GameEnvironment(String proxy, boolean newAuthentication) {
        if (proxy == null) {
            this.proxy = new ProxyConfig();
        } else {
            String[] parts = proxy.split(":");
            if (parts.length == 2) {
                this.proxy = new ProxyConfig(Proxy.Type.SOCKS, parts[0],
                        Integer.valueOf(parts[1]), null, null, newAuthentication);
            } else {
                this.proxy = new ProxyConfig(Proxy.Type.SOCKS, parts[0],
                        Integer.valueOf(parts[1]), parts[2], parts[3], newAuthentication);
            }
        }
    }

    public GameEnvironment(String proxy) {
        this(proxy, true);
    }

    @Override
    public GameSource getSource() {
        return SOURCE;
    }

    @Override
    public String getArchive() {
        return parameters.get("codebase") + parameters.get(ARCHIVE_KEY);
    }

    @Override
    public String getParameter(String key) {
        return parameters.get(key);
    }

    @Override
    public void load() {
        try (JavConfig parser = new JavConfig(getSource().getJavConfig(getLanguage().getId()), getProxy())) {
            parameters.putAll(parser.read());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Language getLanguage() {
        return LANGUAGE;
    }

    @Override
    public ProxyConfig getProxy() {
        return proxy;
    }
}

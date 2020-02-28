package org.rspeer.loader;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

public class ProxyConfig {
    private final String host, username, password;
    private final int port;

    private final Proxy.Type type;

    public ProxyConfig() {
        this(Proxy.Type.DIRECT, null, -1, null, null);
    }

    public ProxyConfig(Proxy.Type type, String host, int port) {
        this(type, host, port, null, null);
    }

    public ProxyConfig(Proxy.Type type, String host, int port, String username, String password) {
        this(type, host, port, username, password, true);
    }

    public ProxyConfig(Proxy.Type type, String host, int port, String username, String password, boolean newAuth) {
        this.type = type;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

        if (host == null) {
            return;
        }

        System.setProperty("proxySet", "true");
        System.setProperty("socksProxyHost", host);
        System.setProperty("socksProxyPort", String.valueOf(port));
        if (username != null) {
            System.setProperty("java.net.socks.username", username);
        }
        if (password != null) {
            System.setProperty("java.net.socks.password", password);
        }

        if (username != null || password != null) {
            Authenticator.setDefault(new ProxyAuth(username, password));
        }

        if (type == Proxy.Type.HTTP) {
            throw new RuntimeException("Only Socks5 proxies are supported");
        }
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String reformat() {
        if (host != null && port != -1) {
            return host
                    + ":" + port + ":" + type.name().toLowerCase() + (username != null && password != null ? ("@" + username + ":" + password) : "");

        }
        return null;
    }

    public Proxy getProxy() {
        if (host != null && port != -1) {
            InetSocketAddress address = new InetSocketAddress(host, port);
            return new Proxy(type, address);
        }
        return Proxy.NO_PROXY;
    }

    @Override
    public String toString() {
        return "ProxyConfig{" +
                "host='" + host + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", type=" + type +
                '}';
    }

    private static class ProxyAuth extends Authenticator {

        private PasswordAuthentication auth;

        private ProxyAuth(String user, String password) {
            auth = new PasswordAuthentication(user, password == null
                    ? new char[]{} : password.toCharArray());
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return auth;
        }
    }
}

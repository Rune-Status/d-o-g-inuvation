package org.rspeer.io;

import org.rspeer.loader.ProxyConfig;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Septron
 * @since September 13, 2018
 */
public final class JavConfig implements Closeable, Consumer<String> {

    private final Map<String, String> parameters = new HashMap<>();
    private final InputStream stream;
    private final ProxyConfig proxy;

    /**
     * @param url   the url to parse
     * @param proxy the proxy to load the page from
     * @throws IOException if there was a problem creating the {@link URL} or opening the stream.
     */
    public JavConfig(String url, ProxyConfig proxy) throws IOException {
        this(new URL(url), proxy);
    }

    /**
     * @param url   the url to parse
     * @param proxy the proxy to load the page from
     * @throws IOException if there was a problem opening the stream.
     */
    public JavConfig(URL url, ProxyConfig proxy) throws IOException {
        this.stream = url.openConnection(proxy.getProxy()).getInputStream();
        this.proxy = proxy;
    }


    /**
     * @throws IOException if there was a problem reading the stream.
     */
    public Map<String, String> read() throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
            buffer.lines().forEach(this);
        }
        return parameters;
    }

    @Override
    public void accept(String line) {
        int separator = line.indexOf('=');
        if (separator != -1) {
            String key = line.substring(0, separator);
            switch (key) {
                case "param": {
                    String value = line.substring(separator + 1);
                    separator = value.indexOf('=');
                    key = value.substring(0, separator);
                    value = value.substring(value.lastIndexOf('=') + 1);
                    parameters.put(key, value);
                    break;
                }
                case "initial_jar":
                case "codebase": {
                    parameters.put(key, line.substring(separator + 1));
                    break;
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}

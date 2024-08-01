package com.github.mangila.yakvs.server.ssl;

import com.github.mangila.yakvs.common.ServerConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class YakvsSslServerDriver {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    public static final ServerConfig SERVER_CONFIG = ServerConfig.load("server.yml");

    static {
        System.setProperty("javax.net.debug", "ssl");
    }

    private YakvsSslServer server;

    public YakvsSslServerDriver(ServerConfig serverConfig, SSLContext sslContext) throws Exception {
        var port = serverConfig.getPort();
        if (port < 0) {
            throw new IllegalArgumentException("Port cannot be less than 0");
        }
        this.server = new YakvsSslServer(port, serverConfig.getName(), sslContext);
    }

    public static void main(String[] args) {
        try {
            var sslContext = SslContextFactory.getInstance("TLS", "", "", "", "");
            var driver = new YakvsSslServerDriver(SERVER_CONFIG, sslContext);
            driver.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        EXECUTOR_SERVICE.execute(server);
    }

    public void close() {
        try {
            server.close();
            EXECUTOR_SERVICE.shutdown();
            while (!EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS)) {
                EXECUTOR_SERVICE.shutdownNow();
            }
        } catch (InterruptedException e) {
            EXECUTOR_SERVICE.shutdownNow();
        }
    }
}

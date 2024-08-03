package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.common.ServerConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class YakvsDriver {

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load("server.yml");

    static {
        System.setProperty("javax.net.debug", "ssl");
    }

    private final ExecutorService executorService;
    @Getter
    private final YakvsServer server;

    public YakvsDriver(ServerConfig serverConfig, SSLContext sslContext) {
        var port = serverConfig.getPort();
        if (port < 0) {
            throw new IllegalArgumentException("Port cannot be less than 0");
        }
        this.executorService = Executors.newSingleThreadExecutor(Thread.ofVirtual().factory());
        this.server = new YakvsServer(port, serverConfig.getName(), sslContext);
    }

    public static void main(String[] args) {
        try {
            var sslContext = SslContextFactory.getInstance("TLS", "", "", "", "");
            var driver = new YakvsDriver(SERVER_CONFIG, sslContext);
            driver.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        log.info("Initialize Driver");
        executorService.execute(server);
    }

    public void close() {
        try {
            log.info("Close Driver");
            server.stop();
            executorService.shutdown();
            while (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

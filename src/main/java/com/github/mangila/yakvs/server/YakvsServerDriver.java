package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.common.ServerConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class YakvsServerDriver {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load("server-plain.yml");

    private YakvsPlainServer server;

    public YakvsServerDriver(ServerConfig serverConfig) {
        if (serverConfig.getPort() < 0) {
            throw new IllegalArgumentException("Port cannot be less than 0");
        }
        this.server = new YakvsPlainServer(serverConfig.getPort(), serverConfig.getName());
    }

    public static void main(String[] args) {
        var driver = new YakvsServerDriver(SERVER_CONFIG);
        driver.initialize();
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

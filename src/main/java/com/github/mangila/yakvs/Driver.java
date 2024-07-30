package com.github.mangila.yakvs;

import com.github.mangila.yakvs.common.ServerConfig;
import com.github.mangila.yakvs.server.PlainServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class Driver {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load();

    private PlainServer server;

    public static void main(String[] args) {
        var driver = new Driver();
        driver.initialize();
    }

    public void initialize() {
        if (SERVER_CONFIG.getPort() < 0) {
            throw new IllegalArgumentException("Port cannot be less than 0");
        }
        this.server = new PlainServer(SERVER_CONFIG.getPort());
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

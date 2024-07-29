package com.github.mangila.yakvs;

import com.github.mangila.yakvs.common.ServerConfig;
import com.github.mangila.yakvs.server.PlainServer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Driver {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load();
    private boolean open;

    public static void main(String[] args) {
        var driver = new Driver();
        driver.initialize();
    }

    public void initialize() {
        if (SERVER_CONFIG.getPort() < 0) {
            throw new IllegalArgumentException("Port cannot be less than 0");
        }
        EXECUTOR_SERVICE.execute(new PlainServer(SERVER_CONFIG.getPort()));
        open = Boolean.TRUE;
    }

    public boolean isOpen() {
        return open;
    }
}

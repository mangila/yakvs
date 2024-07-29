package com.github.mangila.yakvs;

import com.github.mangila.yakvs.common.ServerConfig;
import com.github.mangila.yakvs.server.PlainServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Driver {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load();

    public static void main(String[] args) throws Exception {
        try (var serverThread = EXECUTOR_SERVICE) {
            var port = SERVER_CONFIG.getPort();
            if (port < 0) {
                throw new IllegalArgumentException("Port cannot be less than 0");
            }
            log.info("Starting new PlainServer bound to port: {}", port);
            var server = new PlainServer(port);
            serverThread.submit(server);
        }
    }
}

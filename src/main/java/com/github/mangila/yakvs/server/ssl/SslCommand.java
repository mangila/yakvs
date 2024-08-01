package com.github.mangila.yakvs.server.ssl;

import com.github.mangila.yakvs.engine.Engine;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLSocket;

@Slf4j
public class SslCommand implements Runnable {

    private final SSLSocket socket;
    private final Engine engine;

    public SslCommand(SSLSocket socket, Engine engine) {
        this.socket = socket;
        this.engine = engine;
    }

    @Override
    public void run() {
        log.info("Hello");
    }

}

package com.github.mangila.yakvs.server.ssl;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;

import javax.net.ssl.SSLSocket;

public class SslWorker implements Runnable {

    private final SSLSocket socket;
    private final Parser parser;
    private final Engine engine;

    public SslWorker(SSLSocket socket, Parser parser, Engine engine) {
        this.socket = socket;
        this.parser = parser;
        this.engine = engine;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }

}

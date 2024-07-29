package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;

import java.net.Socket;

public class PlainWorker implements Runnable {

    private final Socket socket;
    private final Parser parser;
    private final Engine engine;

    public PlainWorker(Socket socket, Parser parser, Engine engine) {
        this.socket = socket;
        this.parser = parser;
        this.engine = engine;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
    }
}

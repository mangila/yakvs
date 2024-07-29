package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;
import com.github.mangila.yakvs.engine.Storage;

import javax.net.ServerSocketFactory;
import java.io.IOException;

public class PlainServer implements Runnable, Server {

    private final int port;
    private final Parser parser;
    private final Engine engine;
    private final ServerSocketFactory factory;

    public PlainServer(int port) {
        this.port = port;
        this.parser = new Parser();
        this.engine = new Engine(new Storage());
        this.factory = ServerSocketFactory.getDefault();
    }

    @Override
    public void start() throws IOException {
        try (var serverSocket = factory.createServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                var socket = serverSocket.accept();
                Server.VIRTUAL_POOL.submit(new PlainWorker(socket, parser, engine));
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            stop();
        }
    }
}
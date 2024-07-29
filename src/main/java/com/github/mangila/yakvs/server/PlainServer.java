package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.engine.*;
import lombok.extern.slf4j.Slf4j;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
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
    public void run() {
        try {
            start();
        } catch (Exception e) {
            stop();
        }
    }

    @Override
    public void start() throws IOException, InterruptedException {
        log.info("Accepting connections on PlainServer bound to port: {}", port);
        try (var serverSocket = factory.createServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                var socket = serverSocket.accept();
                TimeUnit.MILLISECONDS.sleep(100);
                Server.VIRTUAL_POOL.submit(new PlainWorker(socket, parser, engine));
            }
        }
    }

    @Override
    public void stop() {
        engine.execute(Query.builder()
                .keyword(Keyword.SAVE)
                .build());
        engine.execute(Query.builder()
                .keyword(Keyword.DUMP)
                .build());
    }
}

package com.github.mangila.yakvs.server.ssl;

import com.github.mangila.yakvs.engine.*;
import com.github.mangila.yakvs.server.Server;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SslServer implements Runnable, Server {

    private final int port;
    private final Parser parser;
    private final Engine engine;
    private final SSLServerSocketFactory factory;

    public SslServer(int port, SSLContext sslContext) {
        this.port = port;
        this.parser = new Parser();
        this.engine = new Engine(new Storage());
        this.factory = sslContext.getServerSocketFactory();
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
        log.info("Accepting connections on SslServer bound to port: {}", port);
        try (var serverSocket = factory.createServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                var socket = (SSLSocket) serverSocket.accept();
                TimeUnit.MILLISECONDS.sleep(100);
                Server.VIRTUAL_POOL.submit(new SslWorker(socket, parser, engine));
            }
        }
    }

    @Override
    public void stop() {
        engine.execute(Query.builder()
                .keyword(Keyword.DUMP)
                .build());
    }
}

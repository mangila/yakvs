package com.github.mangila.yakvs.server.ssl;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;
import com.github.mangila.yakvs.engine.Storage;
import com.github.mangila.yakvs.server.PlainWorker;
import com.github.mangila.yakvs.server.Server;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

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
    public void start() throws IOException {
        log.info("Accepting connections on SslServer bound to port: {}", port);
        try (var serverSocket = factory.createServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                var socket = (SSLSocket) serverSocket.accept();
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

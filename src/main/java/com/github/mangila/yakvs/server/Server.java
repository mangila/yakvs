package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final int bufferSize;
    private final Parser parser;
    private final Engine engine;
    private final Selector selector;
    private final ExecutorService pool = Executors.newFixedThreadPool(2);

    public Server(int port, int bufferSize, Parser parser, Engine engine) throws IOException {
        this.port = port;
        this.bufferSize = bufferSize;
        this.parser = parser;
        this.engine = engine;
        this.selector = Selector.open();
    }

    public void start() throws IOException {
        var serverAcceptHandler = new ServerAcceptHandler(port, selector);
        var serverSessionHandler = new ServerSessionHandler(bufferSize, selector, parser, engine);
        pool.submit(serverAcceptHandler);
        pool.submit(serverSessionHandler);
    }

    public static void closeChannel(SelectionKey selectionKey) {
        try {
            selectionKey.attach(null);
            selectionKey.cancel();
            selectionKey.channel().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

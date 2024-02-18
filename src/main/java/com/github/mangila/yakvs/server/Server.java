package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;
import com.github.mangila.yakvs.engine.storage.FileStorage;
import com.github.mangila.yakvs.engine.storage.InMemoryStorage;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class Server {
    private final int port;
    private final SessionHandler sessionHandler;

    public Server(int port, int bufferSize) {
        this.port = port;
        this.sessionHandler = new SessionHandler(new Parser(), new Engine(new FileStorage(), new InMemoryStorage()), bufferSize);
    }

    public void start(SSLContext sslContext) throws IOException {
        try (var server = ServerSocketChannel.open();
             var selector = Selector.open()) {
            server.configureBlocking(Boolean.FALSE);
            server.register(selector, SelectionKey.OP_ACCEPT);
            server.bind(new InetSocketAddress(port));
            while (selector.isOpen() && server.isOpen()) {
                selector.selectNow(selectionKey -> {
                    if (selectionKey.isValid() && selectionKey.isAcceptable()) {
                        sessionHandler.accept(server, selector, selectionKey, sslContext);
                    }
                    if (selectionKey.isValid() && selectionKey.isReadable()) {
                        sessionHandler.readTls(selectionKey);
                    }
                    if (selectionKey.isValid() && selectionKey.isWritable()) {
                        sessionHandler.writeTls(selectionKey);
                    }
                });
            }
        }
    }

    public void start() throws IOException {
        try (var server = ServerSocketChannel.open();
             var selector = Selector.open()) {
            server.configureBlocking(Boolean.FALSE);
            server.register(selector, SelectionKey.OP_ACCEPT);
            server.bind(new InetSocketAddress(port));
            while (selector.isOpen() && server.isOpen()) {
                selector.selectNow(selectionKey -> {
                    try {
                        if (selectionKey.isValid() && selectionKey.isAcceptable()) {
                            sessionHandler.accept(server, selector);
                        }
                        if (selectionKey.isValid() && selectionKey.isReadable()) {
                            sessionHandler.read(selectionKey);
                        }
                        if (selectionKey.isValid() && selectionKey.isWritable()) {
                            sessionHandler.write(selectionKey);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        selectionKey.cancel();
                    }
                });
            }
        }
    }
}

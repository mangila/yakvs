package com.github.mangila.yakvs.server;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.UUID;

@Slf4j
public class ServerAcceptHandler implements Runnable {

    private final int port;
    private final Selector selector;

    public ServerAcceptHandler(int port, Selector selector) {
        this.port = port;
        this.selector = selector;
    }

    @Override
    public void run() {
        try (var server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(port), 250);
            while (server.isOpen()) {
                SocketChannel channel = server.accept();
                channel.configureBlocking(Boolean.FALSE);
                var session = Session.builder()
                        .sessionId(UUID.randomUUID().toString())
                        .build();
                channel.register(selector, SelectionKey.OP_READ, session);
            }
        } catch (IOException e) {
            log.error("ERR", e);
            throw new RuntimeException(e);
        }
    }
}

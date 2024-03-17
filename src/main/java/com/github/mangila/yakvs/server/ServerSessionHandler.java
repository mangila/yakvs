package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.common.EndOfStreamException;
import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;
import com.github.mangila.yakvs.engine.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public class ServerSessionHandler implements Runnable {

    private static final String POISON_PILL = "POISON_PILL";
    private static final String ERROR_MESSAGE = "Error: input -> '%s' is not a valid syntax";
    private final int bufferSize;
    private final Selector selector;
    private final Parser parser;
    private final Engine engine;

    public ServerSessionHandler(int bufferSize, Selector selector, Parser parser, Engine engine) {
        this.selector = selector;
        this.parser = parser;
        this.engine = engine;
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
        try {
            while (selector.isOpen()) {
                selector.selectNow(selectionKey -> {
                    try {
                        var channel = (SocketChannel) selectionKey.channel();
                        var session = (Session) selectionKey.attachment();
                        var buffer = ByteBuffer.allocate(bufferSize);
                        int bytes = channel.read(buffer);
                        if (bytes == -1) {
                            throw new EndOfStreamException();
                        }
                        String request = new String(buffer.array(), 0, bytes, StandardCharsets.UTF_8);
                        if (request.equals(POISON_PILL)) {
                            throw new EndOfStreamException();
                        }
                        Optional<Query> query = parser.parse(request);
                        var value = query.map(engine::execute).orElseGet(() -> String.format(ERROR_MESSAGE, request).getBytes());
                        buffer = ByteBuffer.wrap(value);
                        while (buffer.hasRemaining()) {
                            channel.write(buffer);
                        }
                        selectionKey.interestOps(SelectionKey.OP_READ);
                    } catch (Exception e) {
                        if (e instanceof EndOfStreamException) {
                            // ignore
                        } else {
                            log.error("ERR", e);
                            Server.closeChannel(selectionKey);
                        }
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

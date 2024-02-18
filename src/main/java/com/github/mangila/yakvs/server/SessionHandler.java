package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.common.EndOfStreamException;
import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;
import com.github.mangila.yakvs.engine.query.Query;
import tlschannel.DirectBufferAllocator;
import tlschannel.HeapBufferAllocator;
import tlschannel.ServerTlsChannel;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SessionHandler {
    private static final String POISON_PILL = "POISON_PILL";
    private static final String ERROR_MESSAGE = "Error: input -> '%s' is not a valid syntax";
    private final Parser parser;
    private final Engine engine;
    private final int bufferSize;

    public SessionHandler(Parser parser, Engine engine, int bufferSize) {
        this.parser = parser;
        this.engine = engine;
        this.bufferSize = bufferSize;
    }

    public void accept(ServerSocketChannel server,
                       Selector selector,
                       SelectionKey selectionKey,
                       SSLContext sslContext) {
//        try {
//            SocketChannel channel = server.accept();
//            channel.configureBlocking(Boolean.FALSE);
//            var tlsChannel = ServerTlsChannel.newBuilder(channel, sslContext)
//                    .withPlainBufferAllocator(new HeapBufferAllocator())
//                    .withEncryptedBufferAllocator(new DirectBufferAllocator())
//                    .build();
//            var session = Session.builder()
//                    .readBuffer(ByteBuffer.allocate(bufferSize))
//                    .tlsChannel(tlsChannel)
//                    .build();
//            channel.register(selector, SelectionKey.OP_READ, session);
//        } catch (IOException e) {
//            close(selectionKey);
//        }
    }

    public void accept(ServerSocketChannel server,
                       Selector selector) throws IOException {
        SocketChannel channel = server.accept();
        channel.configureBlocking(Boolean.FALSE);
        var session = Session.builder()
                .readBuffer(ByteBuffer.allocate(bufferSize))
                .build();
        channel.register(selector, SelectionKey.OP_READ, session);
    }

    public void read(SelectionKey selectionKey) throws IOException {
        var channel = (SocketChannel) selectionKey.channel();
        var session = (Session) selectionKey.attachment();
        var readBuffer = session.getReadBuffer().clear();
        int bytes = channel.read(readBuffer);
        if (bytes == -1) {
            throw new EndOfStreamException();
        }
        String request = new String(readBuffer.array(), 0, bytes, StandardCharsets.UTF_8);
        if (request.equals(POISON_PILL)) {
            throw new EndOfStreamException();
        }
        Optional<Query> query = parser.parse(request);
        String value = query.map(engine::execute)
                .orElseGet(() -> String.format(ERROR_MESSAGE, request));
        session.setWriteBuffer(ByteBuffer.wrap(value.getBytes()));
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    public void write(SelectionKey selectionKey) throws IOException {
        var channel = (SocketChannel) selectionKey.channel();
        var session = (Session) selectionKey.attachment();
        var buffer = session.getWriteBuffer();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        selectionKey.interestOps(SelectionKey.OP_READ);
    }

    public void readTls(SelectionKey selectionKey) {
//        try {
//            TlsChannel tlsChannel = (TlsChannel) selectionKey.attachment();
//            READ_BUFFER.clear();
//            int bytes = tlsChannel.read(READ_BUFFER);
//            if (bytes == -1) {
//                throw new EndOfStreamException();
//            }
//            String request = new String(READ_BUFFER.array(), 0, bytes, StandardCharsets.UTF_8);
//            if (request.equals(POISON_PILL)) {
//                throw new EndOfStreamException();
//            }
//            Optional<Query> query = parser.parse(request);
//            String value = query.map(engine::execute)
//                    .orElseGet(() -> String.format(ERROR_MESSAGE, request));
//            var writeBuffer = ByteBuffer.wrap(value.getBytes());
//            while (writeBuffer.hasRemaining()) {
//                tlsChannel.write(writeBuffer);
//            }
//        } catch (IOException | EndOfStreamException e) {
//            close(selectionKey);
//        }
    }

    public void writeTls(SelectionKey selectionKey) {

    }

    private void close(SelectionKey selectionKey) throws IOException {
        selectionKey.cancel();
        selectionKey.channel().close();
    }
}

package com.github.mangila.yakvs.common;

import com.github.mangila.proto.Entry;
import com.github.mangila.proto.Keyword;
import com.github.mangila.proto.Query;
import com.github.mangila.proto.Response;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

@Slf4j
public class YakvsClient {

    private final String host;
    private final int port;
    private final SSLContext sslContext;
    private final SSLSocketFactory socketFactory;
    private SSLSocket sslSocket;

    public YakvsClient(String host,
                       int port,
                       SSLContext sslContext) {
        this.host = host;
        this.port = port;
        this.sslContext = sslContext;
        this.socketFactory = sslContext.getSocketFactory();
    }

    public Response execute(Query query) {
        try {
            log.info(query.toString());
            query.writeDelimitedTo(sslSocket.getOutputStream());
            var r = Response.parseDelimitedFrom(sslSocket.getInputStream());
            log.info(r.toString());
            return r;
        } catch (IOException e) {
            log.error("ERR", e);
            throw new ClientException(e.getMessage());
        }
    }

    public void connect() {
        try {
            this.sslSocket = (SSLSocket) socketFactory.createSocket(host, port);
            sslSocket.setUseClientMode(Boolean.TRUE);
            sslSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
        } catch (IOException e) {
            log.error("ERR", e);
            throw new ClientException(e.getMessage());
        }
    }

    public boolean isConnected() {
        return !this.sslSocket.isClosed();
    }

    public void disconnect() {
        try {
            this.sslSocket.close();
        } catch (IOException e) {
            log.error("ERR", e);
            throw new ClientException(e.getMessage());
        }
    }

    public Response get(Entry entry) {
        var query = Query.newBuilder()
                .setKeyword(Keyword.GET)
                .setEntry(entry)
                .build();
        return execute(query);
    }

    public Response set(Entry entry) {
        var query = Query.newBuilder()
                .setKeyword(Keyword.SET)
                .setEntry(entry)
                .build();
        return execute(query);
    }

    public Response delete(Entry entry) {
        var query = Query.newBuilder()
                .setKeyword(Keyword.DELETE)
                .setEntry(entry)
                .build();
        return execute(query);
    }

    public Response count() {
        var query = Query.newBuilder()
                .setKeyword(Keyword.COUNT)
                .setEntry(Entry.getDefaultInstance())
                .build();
        return execute(query);
    }

    public Response keys() {
        var query = Query.newBuilder()
                .setKeyword(Keyword.KEYS)
                .setEntry(Entry.getDefaultInstance())
                .build();
        return execute(query);
    }

    public Response flush() {
        var query = Query.newBuilder()
                .setKeyword(Keyword.FLUSH)
                .setEntry(Entry.getDefaultInstance())
                .build();
        return execute(query);
    }

    public Response save() {
        var query = Query.newBuilder()
                .setKeyword(Keyword.SAVE)
                .setEntry(Entry.getDefaultInstance())
                .build();
        return execute(query);
    }
}

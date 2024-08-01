package com.github.mangila.yakvs.client;

import com.github.mangila.proto.Entry;
import com.github.mangila.proto.Keyword;
import com.github.mangila.proto.Query;
import com.github.mangila.proto.Response;
import com.github.mangila.yakvs.common.ClientException;
import lombok.extern.slf4j.Slf4j;

import javax.net.SocketFactory;

@Slf4j
public class YakvsPlainClient implements YakvsClient {

    private final String host;
    private final int port;
    private final SocketFactory socketFactory;

    public YakvsPlainClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.socketFactory = SocketFactory.getDefault();
    }

    @Override
    public Response get(Entry entry) {
        var query = Query.newBuilder()
                .setKeyword(Keyword.GET)
                .setEntry(entry)
                .build();
        return execute(query);
    }

    @Override
    public Response set(Entry entry) {
        var query = Query.newBuilder()
                .setKeyword(Keyword.SET)
                .setEntry(entry)
                .build();
        return execute(query);
    }

    @Override
    public Response delete(Entry entry) {
        var query = Query.newBuilder()
                .setKeyword(Keyword.DELETE)
                .setEntry(entry)
                .build();
        return execute(query);
    }

    @Override
    public Response count() {
        var query = Query.newBuilder()
                .setKeyword(Keyword.COUNT)
                .setEntry(Entry.newBuilder().build())
                .build();
        return execute(query);
    }

    @Override
    public Response keys() {
        var query = Query.newBuilder()
                .setKeyword(Keyword.KEYS)
                .setEntry(Entry.newBuilder().build())
                .build();
        return execute(query);
    }

    @Override
    public Response flush() {
        var query = Query.newBuilder()
                .setKeyword(Keyword.FLUSH)
                .setEntry(Entry.newBuilder().build())
                .build();
        return execute(query);
    }

    @Override
    public Response save() {
        var query = Query.newBuilder()
                .setKeyword(Keyword.SAVE)
                .setEntry(Entry.newBuilder().build())
                .build();
        return execute(query);
    }

    @Override
    public Response execute(Query query) {
        try {
            var socket = this.socketFactory.createSocket(host, port);
            var output = socket.getOutputStream();
            query.writeDelimitedTo(output);
            var response = Response.parseDelimitedFrom(socket.getInputStream());
            socket.close();
            return response;
        } catch (Exception e) {
            log.error("ERR", e);
            throw new ClientException(e.getMessage());
        }
    }
}

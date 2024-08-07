package com.github.mangila.yakvs.server;

import com.github.mangila.proto.Query;
import com.github.mangila.yakvs.engine.Engine;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

@Slf4j
public class ClientSession implements Runnable {

    private final Socket socket;
    private final Engine engine;

    public ClientSession(Socket socket, Engine engine) {
        this.socket = socket;
        this.engine = engine;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!socket.isClosed()) {
            var input = socket.getInputStream();
            var query = Query.parseDelimitedFrom(input);
            log.info(query.toString());
            var response = engine.execute(query);
            var output = socket.getOutputStream();
            log.info(response.toString());
            response.writeDelimitedTo(output);
        }
    }
}

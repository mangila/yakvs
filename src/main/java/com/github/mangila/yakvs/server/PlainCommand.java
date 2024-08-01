package com.github.mangila.yakvs.server;

import com.github.mangila.proto.Query;
import com.github.mangila.yakvs.engine.Engine;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class PlainCommand implements Runnable {

    private final Socket socket;
    private final Engine engine;

    public PlainCommand(Socket socket, Engine engine) {
        this.socket = socket;
        this.engine = engine;
    }

    @Override
    public void run() {
        try {
            var query = Query.parseDelimitedFrom(socket.getInputStream());
            log.info(query.toString());
            var response = engine.execute(query);
            response.writeDelimitedTo(socket.getOutputStream());
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

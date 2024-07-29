package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Slf4j
public class PlainWorker implements Runnable {

    private static final String ERROR_MESSAGE = "Error: input -> '%s' is not a valid syntax";

    private final Socket socket;
    private final Parser parser;
    private final Engine engine;

    public PlainWorker(Socket socket, Parser parser, Engine engine) {
        this.socket = socket;
        this.parser = parser;
        this.engine = engine;
    }

    @Override
    public void run() {
        try (var reader = getReader(); var out = socket.getOutputStream()) {
            StringBuilder request = new StringBuilder();
            while (reader.ready()) {
                var line = reader.readLine();
                request.append(line)
                        .append(" ");
            }
            log.info(request.toString());
            var optionalQuery = parser.parse(request.toString().trim());
            var value = optionalQuery.map(engine::execute)
                    .orElseGet(() -> String.format(ERROR_MESSAGE, request).getBytes());
            out.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
}

package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;
import com.github.mangila.yakvs.engine.Storage;
import lombok.extern.slf4j.Slf4j;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class PlainServer implements Runnable {

    private final int port;
    private final Parser parser;
    private final Engine engine;
    private final ServerSocketFactory serverSocketFactory;
    private final ExecutorService executorService;
    private final AtomicBoolean open;
    private ServerSocket serverSocket;

    public PlainServer(int port) {
        this.port = port;
        this.parser = new Parser();
        this.engine = new Engine(new Storage());
        this.serverSocketFactory = ServerSocketFactory.getDefault();
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.open = new AtomicBoolean(Boolean.FALSE);
    }

    public void open() {
        log.info("Accepting connections on port: {}", port);
        open.set(Boolean.TRUE);
    }

    public boolean isOpen() {
        return open.get();
    }

    public void close() {
        log.info("Closing connections on port: {}", port);
        open.set(Boolean.FALSE);
    }

    @Override
    public void run() {
        this.serverSocket = openServerSocket();
        while (isOpen()) {
            try {
                var client = serverSocket.accept();
                TimeUnit.MILLISECONDS.sleep(100);
                executorService.submit(new PlainWorker(client, parser, engine));
            } catch (SocketTimeoutException e) {
                // ignore
            } catch (IOException e) {
                log.error("ERR", e);
                close();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("ERR", e);
                close();
            }
        }
        closeServerSocket();
    }

    private ServerSocket openServerSocket() {
        try {
            var s = serverSocketFactory.createServerSocket(port);
            s.setSoTimeout(2000);
            open();
            return s;
        } catch (IOException e) {
            log.error("ERR", e);
            throw new RuntimeException(e);
        }
    }

    private void closeServerSocket() {
        try {
            this.serverSocket.close();
            this.executorService.close();
            while (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (IOException e) {
            log.error("ERR", e);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        log.info("{} is closed on port: {}", this.getClass().getSimpleName(), port);
    }
}

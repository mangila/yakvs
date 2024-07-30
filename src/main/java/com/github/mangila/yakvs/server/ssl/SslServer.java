package com.github.mangila.yakvs.server.ssl;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;
import com.github.mangila.yakvs.engine.Storage;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SslServer implements Runnable {

    private final int port;
    private final Parser parser;
    private final Engine engine;
    private final SSLServerSocketFactory serverSocketFactory;
    private final ExecutorService executorService;
    private final AtomicBoolean open;
    private SSLServerSocket serverSocket;

    public SslServer(int port, SSLContext sslContext) {
        this.port = port;
        this.parser = new Parser();
        this.engine = new Engine(new Storage());
        this.serverSocketFactory = sslContext.getServerSocketFactory();
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
                var client = (SSLSocket) serverSocket.accept();
                TimeUnit.MILLISECONDS.sleep(100);
                executorService.submit(new SslWorker(client, parser, engine));
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

    private SSLServerSocket openServerSocket() {
        try {
            var s = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
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

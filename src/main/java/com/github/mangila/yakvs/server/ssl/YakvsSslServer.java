package com.github.mangila.yakvs.server.ssl;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Parser;
import com.github.mangila.yakvs.engine.QueryCache;
import com.github.mangila.yakvs.engine.Storage;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SslServer implements Runnable {

    private final int port;
    private final String name;
    private final Parser parser;
    private final Engine engine;
    private final SSLServerSocketFactory serverSocketFactory;
    private final ExecutorService executorService;
    private final AtomicBoolean open;
    private SSLServerSocket serverSocket;

    public SslServer(int port,
                     String name,
                     SSLContext sslContext) {
        this.port = port;
        this.name = name.concat(".binpb");
        this.parser = new Parser(new QueryCache(new HashMap<>()));
        this.engine = new Engine(new Storage(new ConcurrentHashMap<>(), Paths.get(name)));
        this.serverSocketFactory = sslContext.getServerSocketFactory();
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.open = new AtomicBoolean(Boolean.FALSE);
    }

    public void open() {
        open.set(Boolean.TRUE);
    }

    public boolean isOpen() {
        return open.get();
    }

    public void close() {
        open.set(Boolean.FALSE);
    }

    @Override
    public void run() {
        this.serverSocket = openServerSocket();
        log.info("Accepting connections: {}", serverSocket);
        while (isOpen()) {
            try {
                var client = (SSLSocket) serverSocket.accept();
                TimeUnit.MILLISECONDS.sleep(100);
                executorService.submit(new SslWorker(client, parser, engine));
            } catch (SocketTimeoutException e) {
                // ignore
            } catch (InterruptedException e) {
                log.error("ERR", e);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("ERR", e);
                break;
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
            log.info("Closing connections: {}", serverSocket);
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
    }
}

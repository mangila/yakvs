package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Storage;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class YakvsServer implements Runnable {

    private final int port;
    private final String name;
    private final Engine engine;
    private final SSLContext sslContext;
    private final SSLServerSocketFactory serverSocketFactory;
    private final ExecutorService executorService;
    private final AtomicBoolean running;
    private SSLServerSocket serverSocket;

    public YakvsServer(int port,
                       String name,
                       SSLContext sslContext) {
        this.port = port;
        this.name = name.concat(".binpb");
        this.engine = new Engine(new Storage(Paths.get(this.name)));
        this.sslContext = sslContext;
        this.serverSocketFactory = sslContext.getServerSocketFactory();
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
        this.running = new AtomicBoolean(Boolean.FALSE);
    }

    public boolean isRunning() {
        return running.get();
    }

    public void stop() {
        running.set(Boolean.FALSE);
    }

    @Override
    public void run() {
        this.serverSocket = openServerSocket();
        running.set(Boolean.TRUE);
        log.info("Accepting connections {}: {}", name, serverSocket);
        while (isRunning()) {
            try {
                var client = serverSocket.accept();
                executorService.submit(new ClientSession(client, engine));
            } catch (SocketTimeoutException e) {
                // ignore
            } catch (Exception e) {
                log.error("ERR", e);
                stop();
            }
        }
        closeServerSocket();
    }

    private SSLServerSocket openServerSocket() {
        try {
            var s = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
            s.setSoTimeout((int) TimeUnit.SECONDS.toSeconds(2));
            s.setEnabledProtocols(new String[]{"TLSv1.3"});
            s.setNeedClientAuth(true);
            return s;
        } catch (IOException e) {
            log.error("ERR", e);
            throw new RuntimeException(e);
        }
    }

    private void closeServerSocket() {
        try {
            log.info("Closing connections {}: {}", name, serverSocket);
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

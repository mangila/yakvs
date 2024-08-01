package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.engine.Engine;
import com.github.mangila.yakvs.engine.Storage;
import lombok.extern.slf4j.Slf4j;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class YakvsPlainServer implements Runnable {

    private final int port;
    private final String name;
    private final Engine engine;
    private final ServerSocketFactory serverSocketFactory;
    private final ExecutorService executorService;
    private final AtomicBoolean open;
    private ServerSocket serverSocket;

    public YakvsPlainServer(int port, String name) {
        this.port = port;
        this.name = name.concat(".binpb");
        this.engine = new Engine(new Storage(Paths.get(this.name)));
        this.serverSocketFactory = ServerSocketFactory.getDefault();
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
        log.info("Accepting connections {}: {}", name, serverSocket);
        while (isOpen()) {
            try {
                var client = serverSocket.accept();
                TimeUnit.MILLISECONDS.sleep(100);
                executorService.submit(new PlainCommand(client, engine));
            } catch (SocketTimeoutException e) {
                // ignore
            } catch (Exception e) {
                log.error("ERR", e);
                close();
                break;
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
            log.info("Closing connections {}: {}", name, serverSocket);
            close();
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

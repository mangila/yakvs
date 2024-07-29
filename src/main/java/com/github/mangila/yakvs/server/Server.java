package com.github.mangila.yakvs.server;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface Server {

    ExecutorService VIRTUAL_POOL = Executors.newVirtualThreadPerTaskExecutor();

    void start() throws IOException, InterruptedException;

    void stop();
}

package com.github.mangila.yakvs.server;

import com.github.mangila.yakvs.common.ServerConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class YakvsDriver {

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load("server.yml");

    static {
        System.setProperty("javax.net.debug", "ssl");
    }

    private final ExecutorService executorService;
    @Getter
    private final YakvsServer server;

    public YakvsDriver(ServerConfig serverConfig, SSLContext sslContext) {
        var port = serverConfig.getPort();
        if (port < 0) {
            throw new IllegalArgumentException("Port cannot be less than 0");
        }
        var needClientAuth = true;
        if (serverConfig.isQuickstart()) {
            needClientAuth = false;
        }
        this.executorService = Executors.newSingleThreadExecutor(Thread.ofVirtual().factory());
        this.server = new YakvsServer(port, serverConfig.getName(), needClientAuth, sslContext);
    }

    public static void main(String[] args) {
        try {
            SSLContext sslContext;
            if (SERVER_CONFIG.isQuickstart()) {
                log.info("Quickstart enabled - this is not recommended for prod");
                sslContext = SslContextFactory.getQuickstartContext();
            } else {
                var keyStoreLocation = System.getProperty("javax.net.ssl.keyStore");
                var keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
                var keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
                Objects.requireNonNull(keyStoreLocation);
                Objects.requireNonNull(keyStorePassword);
                Objects.requireNonNull(keyStoreType);
                var ks = SslContextFactory.getKeystore(keyStoreType, new FileInputStream(keyStoreLocation), keyStorePassword);
                var keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManager.init(ks, keyStorePassword.toCharArray());

                var trustStoreLocation = System.getProperty("javax.net.ssl.trustStore");
                var trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
                var trustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
                Objects.requireNonNull(trustStoreLocation);
                Objects.requireNonNull(trustStorePassword);
                Objects.requireNonNull(trustStoreType);
                var ts = SslContextFactory.getKeystore(trustStoreType, new FileInputStream(trustStoreLocation), trustStorePassword);
                var trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManager.init(ts);
                sslContext = SslContextFactory.getInstance("TLS",
                        keyManager.getKeyManagers(), trustManager.getTrustManagers(),
                        SecureRandom.getInstanceStrong());
            }
            var driver = new YakvsDriver(SERVER_CONFIG, sslContext);
            driver.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        log.info("Initialize Driver");
        executorService.execute(server);
    }

    public void close() {
        try {
            log.info("Close Driver");
            server.stop();
            executorService.shutdown();
            while (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}

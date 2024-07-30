package com.github.mangila.yakvs;

import com.github.mangila.yakvs.common.ServerConfig;
import com.github.mangila.yakvs.server.ssl.CertificateFactory;
import com.github.mangila.yakvs.server.ssl.SslContextFactory;
import com.github.mangila.yakvs.server.ssl.SslServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public class SslDriver {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private static final String SELF_SIGNED_CERT_ALIAS = "mangila-cert";
    public static final ServerConfig SERVER_CONFIG = ServerConfig.load();

    static {
        System.setProperty("javax.net.debug", "ssl");
    }

    private SslServer server;

    public static void main(String[] args) {
        var driver = new SslDriver();
        try {
            driver.initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() throws Exception {
        var port = SERVER_CONFIG.getPort();
        if (port < 0) {
            throw new IllegalArgumentException("Port cannot be less than 0");
        }
        var keystoreLocation = System.getProperty("javax.net.ssl.keyStore");
        var keystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        if (SERVER_CONFIG.isSelfSignCertificate()) {
            var certificate = CertificateFactory.createSelfSignedX509Certificate();
            log.info(certificate.toString());
            log.info("Adding self signed certificate to Keystore: {}", keystoreLocation);
            KeyStore keystore = KeyStore.getInstance(
                    new File(keystoreLocation),
                    keystorePassword.toCharArray());
            keystore.setCertificateEntry(SELF_SIGNED_CERT_ALIAS, certificate);
        }
        var sslContext = SslContextFactory.getInstance("TLS", keystoreLocation, keystorePassword);
        this.server = new SslServer(port, sslContext);
        EXECUTOR_SERVICE.execute(server);
    }

    public void close() {
        try {
            server.close();
            EXECUTOR_SERVICE.shutdown();
            while (!EXECUTOR_SERVICE.awaitTermination(5, TimeUnit.SECONDS)) {
                EXECUTOR_SERVICE.shutdownNow();
            }
        } catch (InterruptedException e) {
            EXECUTOR_SERVICE.shutdownNow();
        }
    }

}

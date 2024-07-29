package com.github.mangila.yakvs;

import com.github.mangila.yakvs.common.ServerConfig;
import com.github.mangila.yakvs.server.CertificateFactory;
import com.github.mangila.yakvs.server.SslContextFactory;
import com.github.mangila.yakvs.server.SslServer;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SslDriver {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private static final String SELF_SIGNED_CERT_ALIAS = "mangila-cert";

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load();

    static {
        System.setProperty("javax.net.debug", "ssl");
    }

    public static void main(String[] args) {
        try (var serverThread = EXECUTOR_SERVICE) {
            var port = SERVER_CONFIG.getPort();
            if (port < 0) {
                throw new IllegalArgumentException("Port cannot be less than 0");
            }

            var keystoreLocation = System.getProperty("javax.net.ssl.keyStore");
            var keystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
            if (SERVER_CONFIG.isSelfSignCertificate()) {
                var certificate = CertificateFactory.createSelfSignedX509Certificate();
                log.info(certificate.toString());
                log.info("Adding certificate to Keystore: {}", keystoreLocation);
                KeyStore keystore = KeyStore.getInstance(
                        new File(keystoreLocation),
                        keystorePassword.toCharArray());
                keystore.setCertificateEntry(SELF_SIGNED_CERT_ALIAS, certificate);
            }
            var sslContext = SslContextFactory.getInstance("TLS", keystoreLocation, keystorePassword);
            log.info("Starting new SslServer bound to port: {}", port);
            var server = new SslServer(port, sslContext);
            serverThread.submit(server);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.github.mangila.yakvs.server;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

public class SslTestHelper {

    private static final String SERVER_STORE = "/test-server-certificate.p12";
    private static final String CLIENT_STORE = "/test-client-certificate.p12";
    private static final String PKCS12 = "PKCS12";
    private static final String PASSWORD = "test";

    /**
     * Server certificate in Keystore and Client certificate in server Truststore
     */
    public static SSLContext getServerSslContext() {
        try (var serverStore = getResource(SERVER_STORE);
             var clientStore = getResource(CLIENT_STORE)) {
            var keystore = KeyStore.getInstance(PKCS12);
            keystore.load(serverStore, PASSWORD.toCharArray());
            var keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManager.init(keystore, PASSWORD.toCharArray());
            var truststore = KeyStore.getInstance(PKCS12);
            truststore.load(clientStore, PASSWORD.toCharArray());
            var trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManager.init(truststore);
            var sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManager.getKeyManagers(),
                    trustManager.getTrustManagers(),
                    SecureRandom.getInstanceStrong());
            return sslContext;
        } catch (UnrecoverableKeyException | KeyManagementException | CertificateException | NoSuchAlgorithmException |
                 IOException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Client certificate in Keystore and Server certificate in client Truststore
     */
    public static SSLContext getClientSslContext() {
        try (var serverStore = getResource(SERVER_STORE);
             var clientStore = getResource(CLIENT_STORE)) {
            var keystore = KeyStore.getInstance(PKCS12);
            keystore.load(clientStore, PASSWORD.toCharArray());
            var keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManager.init(keystore, PASSWORD.toCharArray());
            var truststore = KeyStore.getInstance(PKCS12);
            truststore.load(serverStore, PASSWORD.toCharArray());
            var trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManager.init(truststore);
            var sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManager.getKeyManagers(),
                    trustManager.getTrustManagers(),
                    SecureRandom.getInstanceStrong());
            return sslContext;
        } catch (UnrecoverableKeyException | KeyManagementException | CertificateException | KeyStoreException |
                 IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getResource(String resource) {
        return SslTestHelper.class.getResourceAsStream(resource);
    }
}

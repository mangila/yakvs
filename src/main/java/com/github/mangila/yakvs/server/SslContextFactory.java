package com.github.mangila.yakvs.server;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

public class SslContextFactory {

    private static final String QUICKSTART_STORE = "/quickstart-server-certificate.p12";
    private static final String PKCS12 = "PKCS12";
    private static final String QUICKSTART_PASSWORD = "quickstart";

    public static SSLContext getInstance(String protocol,
                                         KeyManager[] keyManagers,
                                         TrustManager[] trustManagers,
                                         SecureRandom secureRandom) throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(keyManagers, trustManagers, secureRandom);
        return sslContext;
    }

    public static KeyStore getKeystore(String type, InputStream location, String password) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        var keystore = KeyStore.getInstance(type);
        keystore.load(location, password.toCharArray());
        return keystore;
    }

    public static SSLContext getQuickstartContext() {
        try (var quickstartStore = SslContextFactory.class.getResourceAsStream(QUICKSTART_STORE)) {
            var keystore = getKeystore(PKCS12, quickstartStore, QUICKSTART_PASSWORD);
            var keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManager.init(keystore, QUICKSTART_PASSWORD.toCharArray());
            return getInstance("TLS", keyManager.getKeyManagers(), null, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

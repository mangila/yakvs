package com.github.mangila.yakvs.server.ssl;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class SslContextFactory {

    public static SSLContext getInstance(String protocol,
                                         String keystoreLocation,
                                         String keystorePassword,
                                         String truststoreLocation,
                                         String truststorePassword)
            throws GeneralSecurityException, IOException {
        KeyStore keystore = KeyStore.getInstance(new File("file"), "password".toCharArray());
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, "password".toCharArray());
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance("PKIX", "SunJSSE");
        trustManagerFactory.init(keystore);
        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                SecureRandom.getInstanceStrong());

        return sslContext;
    }

    public static KeyStore getKeystore(String type, String location, String password) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        var keystore = KeyStore.getInstance(type);
        keystore.load(new FileInputStream(location), password.toCharArray());
        return keystore;
    }
}

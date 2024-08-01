package com.github.mangila.yakvs.server.ssl;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
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

    private static KeyStore getTruststore(String location, String password) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        return KeyStore.getInstance(new File(location), password.toCharArray());
    }

    private static KeyStore getKeystore(String location, String password) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        return KeyStore.getInstance(new File(location), password.toCharArray());
    }
}

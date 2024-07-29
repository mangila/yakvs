package com.github.mangila.yakvs.server;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

public class SslContextFactory {

    public static SSLContext getInstance(String protocol, String file, String password)
            throws GeneralSecurityException, IOException {
        KeyStore keystore = KeyStore.getInstance(new File(file), password.toCharArray());
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, password.toCharArray());
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keystore);
        SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(
                keyManagerFactory.getKeyManagers(),
                trustManagerFactory.getTrustManagers(),
                SecureRandom.getInstanceStrong());

        return sslContext;
    }
}

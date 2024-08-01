package com.github.mangila.yakvs.server.ssl;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CertificateFactory {

    public static X509Certificate createSelfSignedX509Certificate() throws Exception {
        var issuer = new X500NameBuilder()
                .addRDN(BCStyle.CN, "Mangila")
                .addRDN(BCStyle.O, "Mangila Enterprise")
                .addRDN(BCStyle.OU, "Software developing")
                .addRDN(BCStyle.L, "Södermalm")
                .addRDN(BCStyle.ST, "Stockholm")
                .addRDN(BCStyle.C, "Sweden")
                .build();
        var serialNumber = BigInteger.valueOf(11866);
        var notBefore = Date.from(Instant.now());
        var notAfter = Date.from(Instant.now().plus(90, ChronoUnit.DAYS));
        var subject = new X500NameBuilder()
                .addRDN(BCStyle.CN, "YAKVS server")
                .build();
        var rsa = generateKeyPair();
        var signer = new JcaContentSignerBuilder("SHA256WithRSA")
                .setProvider(new BouncyCastleProvider())
                .setSecureRandom(SecureRandom.getInstanceStrong())
                .build(rsa.getPrivate());
        var builder = new JcaX509v3CertificateBuilder(issuer,
                serialNumber,
                notBefore,
                notAfter,
                subject,
                rsa.getPublic());
        return new JcaX509CertificateConverter()
                .setProvider(new BouncyCastleProvider())
                .getCertificate(builder.build(signer));
    }

    private static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048, SecureRandom.getInstanceStrong());
        return keyPairGenerator.generateKeyPair();
    }
}

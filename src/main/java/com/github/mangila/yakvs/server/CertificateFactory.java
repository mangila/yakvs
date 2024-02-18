package com.github.mangila.yakvs.server;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class CertificateFactory {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

//    private X509CertificateHolder createSelfSignedX509Certificate() throws Exception {
//        var issuer = new X500NameBuilder()
//                .addRDN(BCStyle.CN, "Mangila")
//                .addRDN(BCStyle.O, "Mangila Enterprise")
//                .addRDN(BCStyle.OU, "Software developing")
//                .addRDN(BCStyle.L, "Södermalm")
//                .addRDN(BCStyle.ST, "Stockholm")
//                .addRDN(BCStyle.C, "Sweden")
//                .build();
//        var serialNumber = BigInteger.valueOf(11866);
//        var notBefore = Date.from(Instant.now());
//        var notAfter = Date.from(Instant.now().plus(3, ChronoUnit.MONTHS));
//        var subject = new X500NameBuilder()
//                .addRDN(BCStyle.CN, "Pet DB server")
//                .build();
//        var signer = new JcaContentSignerBuilder("SHA256WithRSA")
//                .setProvider(new BouncyCastleProvider())
//                .setSecureRandom(new SecureRandom())
//                .build();
//        var builder = new X509v3CertificateBuilder(issuer, serialNumber, notBefore, notAfter, subject, );
//        return builder.build(signer);
//    }

}

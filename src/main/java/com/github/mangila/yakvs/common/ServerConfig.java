package com.github.mangila.yakvs.common;

@lombok.Getter
@lombok.Setter
public class ServerConfig {
    private int port;
    private String keystore;
    private boolean selfSignCertificate;
    private int bufferSize;
}
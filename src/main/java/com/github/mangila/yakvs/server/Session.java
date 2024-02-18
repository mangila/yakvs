package com.github.mangila.yakvs.server;

import tlschannel.TlsChannel;

import java.nio.ByteBuffer;
import java.util.UUID;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
public class Session {
    private final String sessionId = UUID.randomUUID().toString();
    private TlsChannel tlsChannel;
    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
}

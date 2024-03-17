package com.github.mangila.yakvs.server;

import tlschannel.TlsChannel;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
public class Session {
    private String sessionId;
    private TlsChannel tlsChannel;
}

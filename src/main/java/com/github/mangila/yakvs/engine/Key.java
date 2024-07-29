package com.github.mangila.yakvs.engine;

import lombok.Builder;

@Builder
public record Key(byte[] rawKey) {
}
package com.github.mangila.yakvs.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {

    private final Map<Key, Value> storage = new ConcurrentHashMap<>();

    public byte[] get(Query query) {
        return new byte[0];
    }

    public byte[] set(Query query) {
        return new byte[0];
    }

    public byte[] delete(Query query) {
        return new byte[0];
    }

    public byte[] count() {
        return new byte[0];
    }

    public byte[] dump() {
        return new byte[0];
    }

    public byte[] flush() {
        return new byte[0];
    }

    public byte[] save() {
        return new byte[0];
    }
}

package com.github.mangila.yakvs.engine.storage;

import com.github.mangila.yakvs.engine.Key;
import com.github.mangila.yakvs.engine.Value;
import com.github.mangila.yakvs.engine.query.Query;

import java.util.Map;

public class RemoteStorage implements Storage {
    @Override
    public byte[] get(Query query) {
        return new byte[0];
    }

    @Override
    public byte[] set(Query query) {
        return new byte[0];
    }

    @Override
    public byte[] delete(Query query) {
        return new byte[0];
    }

    @Override
    public byte[] count() {
        return new byte[0];
    }

    @Override
    public byte[] dump() {
        return new byte[0];
    }

    @Override
    public byte[] flush() {
        return new byte[0];
    }

    @Override
    public byte[] save(Map<Key, Value> storage) {
        return new byte[0];
    }
}

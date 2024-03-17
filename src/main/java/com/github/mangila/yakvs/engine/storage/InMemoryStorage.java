package com.github.mangila.yakvs.engine.storage;

import com.github.mangila.yakvs.common.StorageException;
import com.github.mangila.yakvs.engine.Key;
import com.github.mangila.yakvs.engine.Value;
import com.github.mangila.yakvs.engine.query.Query;
import com.google.common.primitives.Ints;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@lombok.Getter
@Slf4j
public class InMemoryStorage implements Storage {

    private final Map<Key, Value> storage = new HashMap<>();

    @Override
    public byte[] get(Query query) {
        var value = storage.get(query.key());
        return Objects.nonNull(value) ? value.rawValue().getBytes() : StorageUtil.NO_KEY_FOUND.getBytes();
    }

    @Override
    public byte[] set(Query query) {
        storage.put(query.key(), query.value());
        return StorageUtil.OK.getBytes();
    }

    @Override
    public byte[] delete(Query query) {
        storage.remove(query.key());
        return StorageUtil.OK.getBytes();
    }

    @Override
    public byte[] count() {
        var count = storage.size();
        return Ints.toByteArray(count);
    }

    @Override
    public byte[] dump() {
        throw new StorageException("DUMP keyword not in use");
    }

    @Override
    public byte[] flush() {
        storage.clear();
        return StorageUtil.OK.getBytes();
    }

    @Override
    public byte[] save(Map<Key, Value> storage) {
        throw new StorageException("SAVE keyword not in use");
    }
}

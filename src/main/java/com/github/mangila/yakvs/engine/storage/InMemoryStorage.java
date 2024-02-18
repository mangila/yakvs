package com.github.mangila.yakvs.engine.storage;

import com.github.mangila.proto.Entry;
import com.github.mangila.yakvs.engine.query.Query;

import java.util.HashMap;
import java.util.Map;

@lombok.Getter
public class InMemoryStorage implements Storage {

    private final Map<String, Entry> storage = new HashMap<>();

    @Override
    public String get(Query query) {
        return null;
    }

    @Override
    public String set(Query query) {
        return null;
    }

    @Override
    public String delete(Query query) {
        return null;
    }

    @Override
    public String count() {
        return null;
    }

    @Override
    public String dump() {
        return null;
    }

    @Override
    public String flush() {
        return null;
    }
}

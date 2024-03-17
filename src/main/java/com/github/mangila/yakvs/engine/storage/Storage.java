package com.github.mangila.yakvs.engine.storage;

import com.github.mangila.proto.Entry;
import com.github.mangila.yakvs.engine.Key;
import com.github.mangila.yakvs.engine.Value;
import com.github.mangila.yakvs.engine.query.Query;

import java.util.Collection;
import java.util.Map;

public interface Storage {
    byte[] get(Query query);

    byte[] set(Query query);

    byte[] delete(Query query);

    byte[] count();

    byte[] dump();

    byte[] flush();

    byte[] save(Map<Key, Value> storage);
}

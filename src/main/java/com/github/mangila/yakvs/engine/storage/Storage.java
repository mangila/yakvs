package com.github.mangila.yakvs.engine.storage;

import com.github.mangila.yakvs.engine.query.Query;

public interface Storage {
    String get(Query query);

    String set(Query query);

    String delete(Query query);

    String count();

    String dump();

    String flush();
}

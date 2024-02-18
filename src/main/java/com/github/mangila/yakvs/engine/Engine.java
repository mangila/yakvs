package com.github.mangila.yakvs.engine;

import com.github.mangila.yakvs.engine.query.Query;
import com.github.mangila.yakvs.engine.storage.FileStorage;
import com.github.mangila.yakvs.engine.storage.InMemoryStorage;

public class Engine {
    private final FileStorage fileStorage;
    private final InMemoryStorage inMemoryStorage;

    public Engine(FileStorage fileStorage, InMemoryStorage inMemoryStorage) {
        this.fileStorage = fileStorage;
        this.inMemoryStorage = inMemoryStorage;
    }

    public String execute(Query query) {
        return switch (query.keyword()) {
            case GET -> inMemoryStorage.get(query);
            case SET -> inMemoryStorage.set(query);
            case DELETE -> inMemoryStorage.delete(query);
            case COUNT -> inMemoryStorage.count();
            case DUMP -> inMemoryStorage.dump();
            case FLUSH -> inMemoryStorage.flush();
            case SAVE -> fileStorage.save(inMemoryStorage.getStorage().values());
        };
    }
}

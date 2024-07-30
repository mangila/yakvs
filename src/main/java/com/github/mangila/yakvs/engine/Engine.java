package com.github.mangila.yakvs.engine;

import com.github.mangila.yakvs.common.StorageException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Engine {

    private final Storage storage;

    public Engine(Storage storage) {
        this.storage = storage;
    }

    public byte[] execute(Query query) {
        try {
            return switch (query.getKeyword()) {
                case GET -> storage.get(query);
                case SET -> storage.set(query);
                case DELETE -> storage.delete(query);
                case COUNT -> storage.count();
                case KEYS -> storage.keys();
                case FLUSH -> storage.flush();
                case SAVE -> storage.save();
                case null -> throw new StorageException("Not a valid Query");
            };
        } catch (Exception e) {
            log.error("ERR", e);
            throw new StorageException(e.getMessage());
        }
    }
}

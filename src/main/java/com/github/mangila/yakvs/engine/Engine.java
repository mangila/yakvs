package com.github.mangila.yakvs.engine;

import com.github.mangila.yakvs.common.StorageException;
import com.github.mangila.yakvs.engine.query.Query;
import com.github.mangila.yakvs.engine.storage.FileStorage;
import com.github.mangila.yakvs.engine.storage.InMemoryStorage;
import com.github.mangila.yakvs.engine.storage.RemoteStorage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Engine {
    private final FileStorage fileStorage;
    private final InMemoryStorage inMemoryStorage;
    private final RemoteStorage remoteStorage;

    public Engine(FileStorage fileStorage, InMemoryStorage inMemoryStorage, RemoteStorage remoteStorage) {
        this.fileStorage = fileStorage;
        this.inMemoryStorage = inMemoryStorage;
        this.remoteStorage = remoteStorage;
    }

    public byte[] execute(Query query) {
        try {
            return switch (query.keyword()) {
                case GET -> inMemoryStorage.get(query);
                case SET -> inMemoryStorage.set(query);
                case DELETE -> inMemoryStorage.delete(query);
                case COUNT -> inMemoryStorage.count();
                case DUMP -> fileStorage.dump();
                case FLUSH -> inMemoryStorage.flush();
                case SAVE -> fileStorage.save(inMemoryStorage.getStorage());
                case null -> throw new StorageException("Not a valid Query");
            };
        } catch (Exception e) {
            log.error("ERR", e);
            throw new StorageException(e.getMessage());
        }
    }
}

package com.github.mangila.yakvs.engine.storage;

import com.github.mangila.proto.Entry;
import com.github.mangila.yakvs.engine.Key;
import com.github.mangila.yakvs.engine.Value;
import com.github.mangila.yakvs.engine.query.Query;
import com.google.common.primitives.Longs;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.github.mangila.yakvs.engine.storage.StorageUtil.ERR;
import static com.github.mangila.yakvs.engine.storage.StorageUtil.OK;

@Slf4j
public class FileStorage implements Storage {

    public static final Path FILE_STORAGE_DIRECTORY = Paths.get("data");
    public static final String FILE_EXTENSION_BINPB = ".binpb";

    static {
        try {
            if (!Files.isDirectory(FILE_STORAGE_DIRECTORY)) {
                Files.createDirectory(FILE_STORAGE_DIRECTORY);
            }
        } catch (IOException e) {
            log.error(ERR, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] get(Query query) {
        var path = StorageUtil.getFileStoragePath(query.key());
        var entry = get(path);
        return entry.getValue().getBytes();
    }

    private Entry get(Path path) {
        try {
            return Entry.parseFrom(com.google.common.io.Files.toByteArray(path.toFile()));
        } catch (IOException e) {
            log.error(ERR, e);
            return Entry.newBuilder().setKey("-1").setValue("-1").build();
        }
    }

    @Override
    public byte[] set(Query query) {
        var entry = Entry.newBuilder()
                .setKey(query.key().rawKey())
                .setValue(query.value().rawValue())
                .build();
        return set(entry).getBytes();
    }

    private String set(Entry entry) {
        try {
            var rawKey = entry.getKey();
            var path = StorageUtil.getFileStoragePath(new Key(rawKey));
            com.google.common.io.Files.write(entry.toByteArray(), path.toFile());
        } catch (IOException e) {
            log.error(ERR, e);
            return ERR;
        }
        return OK;
    }

    @Override
    public byte[] delete(Query query) {
        var path = StorageUtil.getFileStoragePath(query.key());
        return (delete(path) ? OK : ERR).getBytes();
    }

    private boolean delete(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error(ERR, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] count() {
        try (var stream = Files.walk(FILE_STORAGE_DIRECTORY)) {
            return Longs.toByteArray(stream.count());
        } catch (IOException e) {
            log.error(ERR, e);
            return ERR.getBytes();
        }
    }

    @Override
    public byte[] dump() {
        var csvPath = Paths.get("dump.csv");
        byte[] csvBytes;
        try {
            csvBytes = StorageUtil.writeFileStorageDump(csvPath);
            Files.deleteIfExists(csvPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return csvBytes;
    }

    @Override
    public byte[] flush() {
        try (var stream = Files.walk(FILE_STORAGE_DIRECTORY)) {
            stream.filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            log.error(ERR, e);
                        }
                    });
        } catch (IOException e) {
            log.error(ERR, e);
            return ERR.getBytes();
        }
        return OK.getBytes();
    }

    @Override
    public byte[] save(Map<Key, Value> storage) {
        for (var entry : storage.entrySet()) {
            Thread.ofVirtual().start(() -> set(Entry.newBuilder()
                    .setKey(entry.getKey().rawKey())
                    .setValue(entry.getValue().rawValue())
                    .build()));
        }
        return (OK + " " + storage.size()).getBytes();
    }
}

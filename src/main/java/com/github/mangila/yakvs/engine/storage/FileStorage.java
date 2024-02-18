package com.github.mangila.yakvs.engine.storage;

import com.github.mangila.proto.Entry;
import com.github.mangila.yakvs.engine.query.Query;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;

public class FileStorage implements Storage {

    private static final Path STORAGE_DIRECTORY = Paths.get("data");
    private static final String FILE_EXTENSION = ".binpb";

    static {
        try {
            if (!Files.isDirectory(STORAGE_DIRECTORY)) {
                Files.createDirectory(STORAGE_DIRECTORY);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String get(Query query) {
        var key = query.key().key();
        var entry = get(STORAGE_DIRECTORY.resolve(key + FILE_EXTENSION));
        return entry.getValue();
    }

    private Entry get(Path path) {
        try {
            return Entry.parseFrom(com.google.common.io.Files.toByteArray(path.toFile()));
        } catch (IOException e) {
            return Entry.newBuilder().setKey("-1").setValue("-1").build();
        }
    }

    @Override
    public String set(Query query) {
        var entry = Entry.newBuilder()
                .setKey(query.key().key())
                .setValue(query.value().value())
                .build();
        return set(entry);
    }

    private String set(Entry entry) {
        try {
            var path = STORAGE_DIRECTORY.resolve(entry.getKey() + FILE_EXTENSION);
            com.google.common.io.Files.write(entry.toByteArray(), path.toFile());
        } catch (IOException e) {
            return "FAILED TO WRITE";
        }
        return "OK";
    }

    @Override
    public String delete(Query query) {
        var key = query.key().key();
        return delete(STORAGE_DIRECTORY.resolve(key + FILE_EXTENSION)) ? "OK" : "ERROR";
    }

    private boolean delete(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String count() {
        try (var stream = Files.walk(STORAGE_DIRECTORY)) {
            return String.valueOf(stream.count());
        } catch (IOException e) {
            return "ERROR";
        }
    }

    @Override
    public String dump() {
        try (var writer = Files.newBufferedWriter(Paths.get("dump.csv"), StandardOpenOption.CREATE, StandardOpenOption.WRITE); var stream = Files.walk(STORAGE_DIRECTORY)) {
            writer.write("KEY,VALUE");
            writer.newLine();
            stream.filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)).forEach(path -> {
                var entry = get(path);
                try {
                    writer.write(String.format("%s,%s", entry.getKey(), entry.getValue()));
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            return "ERROR";
        }
        return "OK";
    }

    @Override
    public String flush() {
        try (var stream = Files.walk(STORAGE_DIRECTORY)) {
            stream.filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    // hej
                }
            });
        } catch (IOException e) {
            return "ERROR";
        }
        return "OK";
    }

    public String save(Collection<Entry> entries) {
        for (var entry : entries) {
            set(entry);
        }
        return "SAVED KEYS: " + entries.size();
    }
}

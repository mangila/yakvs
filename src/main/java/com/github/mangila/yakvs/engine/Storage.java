package com.github.mangila.yakvs.engine;

import com.github.mangila.proto.ProtoStorage;
import com.google.common.io.Files;
import com.google.common.primitives.Ints;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class Storage {

    private static final Map<Key, Value> STORAGE = new ConcurrentHashMap<>();
    private static final byte[] OK = "OK".getBytes();
    private static final byte[] ERR = "ERR".getBytes();

    public static final Path BINPB = Path.of("storage.binpb");

    static {
        try {
            if (!java.nio.file.Files.exists(BINPB)) {
                java.nio.file.Files.createFile(BINPB);
            }
            var storage = ProtoStorage.parseFrom(Files.toByteArray(BINPB.toFile()));
            storage.getMapMap().forEach((key, value) -> STORAGE.put(Key.builder()
                            .key(key)
                            .build(),
                    Value.builder()
                            .value(value.toByteArray())
                            .build()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] get(Query query) {
        var value = STORAGE.get(query.getKey());
        if (Objects.isNull(value)) {
            return ERR;
        }
        return value.value();
    }

    public byte[] set(Query query) {
        STORAGE.put(query.getKey(), query.getValue());
        return OK;
    }

    public byte[] delete(Query query) {
        STORAGE.remove(query.getKey());
        return OK;
    }

    public byte[] count() {
        return Ints.toByteArray(STORAGE.size());
    }

    public byte[] keys() {
        try {
            var builder = ProtoStorage.newBuilder()
                    .mergeFrom(ProtoStorage.parseFrom(Files.toByteArray(BINPB.toFile())));
            for (var entry : STORAGE.entrySet()) {
                builder.putMap(entry.getKey().key(), ByteString.EMPTY);
            }
            return builder.build()
                    .getMapMap()
                    .keySet()
                    .stream()
                    .collect(Collectors.joining(System.lineSeparator()))
                    .getBytes();
        } catch (IOException e) {
            log.error("ERR", e);
            return ERR;
        }
    }

    public byte[] flush() {
        STORAGE.clear();
        try {
            java.nio.file.Files.deleteIfExists(BINPB);
        } catch (IOException e) {
            log.error("ERR", e);
            return ERR;
        }
        return OK;
    }

    public byte[] save() {
        try {
            var builder = ProtoStorage.newBuilder();
            for (var entry : STORAGE.entrySet()) {
                builder.putMap(entry.getKey().key(),
                        ByteString.copyFrom(entry.getValue().value()));
            }
            var proto = builder.mergeFrom(ProtoStorage.parseFrom(Files.toByteArray(BINPB.toFile())))
                    .build();
            Files.write(proto.toByteArray(), BINPB.toFile());
            return OK;
        } catch (IOException e) {
            log.error("ERR", e);
            return ERR;
        }
    }
}

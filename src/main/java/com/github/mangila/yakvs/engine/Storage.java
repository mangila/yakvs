package com.github.mangila.yakvs.engine;

import com.github.mangila.proto.ProtoStorage;
import com.github.mangila.proto.Query;
import com.github.mangila.proto.Response;
import com.google.common.io.Files;
import com.google.common.primitives.Ints;
import com.google.protobuf.ByteString;
import lombok.Locked;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Slf4j
public class Storage {

    private static final ByteString OK = ByteString.copyFromUtf8("OK");
    private static final ByteString ERR = ByteString.copyFromUtf8("ERR");

    private ProtoStorage storage;
    private final Path diskStorage;

    public Storage(Path diskStorage) {
        this.diskStorage = diskStorage;
        this.storage = loadFromDisk();
    }

    private ProtoStorage loadFromDisk() {
        try {
            if (!java.nio.file.Files.exists(diskStorage)) {
                java.nio.file.Files.createFile(diskStorage);
            }
            return ProtoStorage.newBuilder()
                    .mergeFrom(ProtoStorage.parseFrom(Files.toByteArray(diskStorage.toFile())))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Locked.Read
    public Response get(Query query) {
        var key = query.getEntry().getKey();
        var value = storage.getMapOrDefault(key, ByteString.EMPTY);
        if (value.isEmpty()) {
            return Response.newBuilder()
                    .setValue(ERR)
                    .build();
        }
        return Response.newBuilder()
                .setValue(value)
                .build();
    }

    @Locked.Write
    public Response set(Query query) {
        var key = query.getEntry().getKey();
        var value = query.getEntry().getValue();
        storage = storage.toBuilder()
                .putMap(key, value)
                .build();
        return Response.newBuilder()
                .setValue(OK)
                .build();
    }

    @Locked.Write
    public Response delete(Query query) {
        var key = query.getEntry().getKey();
        storage = storage.toBuilder()
                .removeMap(key)
                .build();
        return Response.newBuilder()
                .setValue(OK)
                .build();
    }

    @Locked.Read
    public Response count() {
        var count = storage.getMapCount();
        var bytes = Ints.toByteArray(count);
        return Response.newBuilder()
                .setValue(ByteString.copyFrom(bytes))
                .build();
    }

    @Locked.Read
    public Response keys() {
        var proto = ProtoStorage.newBuilder()
                .mergeFrom(storage)
                .mergeFrom(loadFromDisk())
                .build();
        var keys = proto.getMapMap()
                .keySet()
                .stream()
                .collect(Collectors.joining(System.lineSeparator()));
        return Response.newBuilder()
                .setValue(ByteString.copyFromUtf8(keys))
                .build();
    }

    @Locked.Write
    public Response flush() {
        try {
            this.storage = ProtoStorage.newBuilder().build();
            java.nio.file.Files.deleteIfExists(diskStorage);
        } catch (IOException e) {
            log.error("ERR", e);
            return Response.newBuilder()
                    .setValue(ERR)
                    .build();
        }
        return Response.newBuilder()
                .setValue(OK)
                .build();
    }

    @Locked.Write
    public Response save() {
        try {
            var proto = ProtoStorage.newBuilder(loadFromDisk())
                    .mergeFrom(storage)
                    .build();
            Files.write(proto.toByteArray(), diskStorage.toFile());
            return Response.newBuilder()
                    .setValue(OK)
                    .build();
        } catch (IOException e) {
            log.error("ERR", e);
            return Response.newBuilder()
                    .setValue(ERR)
                    .build();
        }
    }
}

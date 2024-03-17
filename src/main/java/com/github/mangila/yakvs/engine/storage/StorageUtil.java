package com.github.mangila.yakvs.engine.storage;

import com.github.mangila.proto.Entry;
import com.github.mangila.yakvs.engine.Key;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.mangila.yakvs.engine.storage.FileStorage.FILE_EXTENSION_BINPB;
import static com.github.mangila.yakvs.engine.storage.FileStorage.FILE_STORAGE_DIRECTORY;

@lombok.experimental.UtilityClass
public class StorageUtil {

    public static final String ERR = "ERR";
    public static final String OK = "OK";
    public static final String NO_KEY_FOUND = "NO KEY FOUND";

    public Path getFileStoragePath(Key key) {
        return FILE_STORAGE_DIRECTORY.resolve(key.rawKey() + FILE_EXTENSION_BINPB);
    }

    public static byte[] writeFileStorageDump(Path csvPath) throws IOException {
        try (var writer = Files.newBufferedWriter(csvPath);
             var stream = Files.walk(FILE_STORAGE_DIRECTORY)) {
            writer.write("KEY,VALUE");
            writer.newLine();
            stream.filter(Files::isRegularFile).forEach(path -> {
                try {
                    var entry = Entry.parseFrom(com.google.common.io.Files.toByteArray(path.toFile()));
                    writer.write(String.format("%s,%s", entry.getKey(), entry.getValue()));
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return com.google.common.io.Files.toByteArray(csvPath.toFile());
    }
}


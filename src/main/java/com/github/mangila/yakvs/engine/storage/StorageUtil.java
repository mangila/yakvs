package com.github.mangila.yakvs.engine.storage;

import com.github.mangila.yakvs.engine.Key;

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
}

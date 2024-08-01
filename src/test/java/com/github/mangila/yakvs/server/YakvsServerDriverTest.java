package com.github.mangila.yakvs.server;

import com.github.mangila.proto.Entry;
import com.github.mangila.proto.Keyword;
import com.github.mangila.proto.Query;
import com.github.mangila.yakvs.client.YakvsClient;
import com.github.mangila.yakvs.client.YakvsPlainClient;
import com.github.mangila.yakvs.common.ServerConfig;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
class YakvsServerDriverTest {

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load("server-test.yml");
    private YakvsServerDriver yakvsServerDriver;
    private YakvsClient yakvsClient;

    @BeforeEach
    void setUp() {
        yakvsServerDriver = new YakvsServerDriver(SERVER_CONFIG);
        yakvsClient = new YakvsPlainClient("localhost", 11866);
    }

    @AfterEach
    void tearDown() {
        yakvsServerDriver.close();
    }

    @Test
    void setAndGet() throws IOException {
        yakvsServerDriver.initialize();
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> yakvsServerDriver.getServer().isOpen());
        var response = yakvsClient.set(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.copyFromUtf8("MyValue"))
                .build());
        assertThat(response.getValue().toStringUtf8()).isEqualTo("OK");
        response = yakvsClient.get(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.EMPTY)
                .build());
        assertThat(response.getValue().toStringUtf8()).isEqualTo("MyValue");
    }
}
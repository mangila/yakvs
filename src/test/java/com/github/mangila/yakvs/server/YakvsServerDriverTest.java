package com.github.mangila.yakvs.server;

import com.github.mangila.proto.Entry;
import com.github.mangila.yakvs.common.YakvsClient;
import com.github.mangila.yakvs.common.ServerConfig;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class YakvsServerDriverTest {

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load("server-test.yml");
    private YakvsServerDriver driver;
    private YakvsClient yakvsClient;

    @BeforeEach
    void setUp() throws Exception {
        this.driver = new YakvsServerDriver(SERVER_CONFIG, SslTestHelper.getServerSslContext());
        this.yakvsClient = new YakvsClient("localhost",
                SERVER_CONFIG.getPort(),
                SslTestHelper.getClientSslContext());
        driver.initialize();
    }

    @AfterEach
    void tearDown() {
        yakvsClient.disconnect();
        driver.close();
    }

    @Test
    void setAndGet() {
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> driver.getServer().isRunning());
        yakvsClient.connect();
        var response = yakvsClient.set(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.copyFromUtf8("MyValue"))
                .build());
        assertThat(response.getValue().toStringUtf8()).isEqualTo("OK");
        response = yakvsClient.get(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.EMPTY)
                .build());
        assertThat(response.getValue()
                .toStringUtf8())
                .isEqualTo("MyValue");
    }
}
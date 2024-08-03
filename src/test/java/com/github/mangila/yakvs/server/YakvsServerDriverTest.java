package com.github.mangila.yakvs.server;

import com.github.mangila.proto.Entry;
import com.github.mangila.yakvs.common.ServerConfig;
import com.github.mangila.yakvs.common.YakvsClient;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class YakvsServerDriverTest {

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load("server-test.yml");
    private static YakvsServerDriver driver;
    private YakvsClient yakvsClient;

    @BeforeEach
    void setUp() throws Exception {
        this.yakvsClient = new YakvsClient("localhost",
                SERVER_CONFIG.getPort(),
                SslTestHelper.getClientSslContext());
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> driver.getServer().isRunning());
        yakvsClient.connect();
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> yakvsClient.isConnected());
    }

    @AfterEach
    void tearDown() {
        yakvsClient.flush();
        yakvsClient.disconnect();
    }

    @BeforeAll
    static void beforeAll() {
        driver = new YakvsServerDriver(SERVER_CONFIG, SslTestHelper.getServerSslContext());
        driver.initialize();
    }

    @AfterAll
    static void afterAll() {
        driver.close();
    }

    @Test
    void get() {
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

    @Test
    void save() {
        yakvsClient.set(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.copyFromUtf8("MyValue"))
                .build());
        var response = yakvsClient.save();
        assertThat(response.getValue()
                .toStringUtf8())
                .isEqualTo("OK");
    }

    @Test
    void delete() {
        yakvsClient.set(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.copyFromUtf8("MyValue"))
                .build());
        var response = yakvsClient.delete(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.EMPTY)
                .build());
        assertThat(response.getValue()
                .toStringUtf8())
                .isEqualTo("OK");
        var count = yakvsClient.count();
        assertThat(count.getValue()
                .toStringUtf8())
                .isEqualTo("0");
    }

    @Test
    void count() {
        yakvsClient.set(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.copyFromUtf8("MyValue"))
                .build());
        var count = yakvsClient.count();
        assertThat(count.getValue()
                .toStringUtf8())
                .isEqualTo("1");
    }

    @Test
    void keys() {
        yakvsClient.set(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.copyFromUtf8("MyValue"))
                .build());
        yakvsClient.set(Entry.newBuilder()
                .setKey("MyKey1")
                .setValue(ByteString.copyFromUtf8("MyValue1"))
                .build());
        var keys = yakvsClient.keys();
        assertThat(keys.getValue()
                .toStringUtf8())
                .isEqualTo("MyKey,MyKey1");
    }

    @Test
    void flush() {
        yakvsClient.set(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.copyFromUtf8("MyValue"))
                .build());
        var response = yakvsClient.flush();
        assertThat(response.getValue()
                .toStringUtf8())
                .isEqualTo("OK");
        response = yakvsClient.count();
        assertThat(response.getValue()
                .toStringUtf8())
                .isEqualTo("0");
    }
}
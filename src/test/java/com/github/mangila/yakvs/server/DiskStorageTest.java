package com.github.mangila.yakvs.server;

import com.github.mangila.proto.Entry;
import com.github.mangila.yakvs.common.ServerConfig;
import com.github.mangila.yakvs.common.YakvsClient;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DiskStorageTest {

    public static final ServerConfig SERVER_CONFIG = ServerConfig.load("server-test.yml");
    private static YakvsDriver driver;
    private YakvsClient yakvsClient;

    @BeforeEach
    void setUp() {
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
        yakvsClient.disconnect();
    }

    @BeforeAll
    static void beforeAll() {
        driver = new YakvsDriver(SERVER_CONFIG, SslTestHelper.getServerSslContext());
        driver.initialize();
    }

    @AfterAll
    static void afterAll() {
        driver.close();
    }

    @Test
    @Order(1)
    void saveToDisk() {
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
    @Order(2)
    void loadFromDisk() {
        var response = yakvsClient.get(Entry.newBuilder()
                .setKey("MyKey")
                .setValue(ByteString.copyFromUtf8("MyValue"))
                .build());
        assertThat(response.getValue()
                .toStringUtf8())
                .isEqualTo("MyValue");
    }
}

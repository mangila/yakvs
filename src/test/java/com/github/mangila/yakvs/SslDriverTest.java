package com.github.mangila.yakvs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

class SslDriverTest {

    private SslDriver sslDriver;

    @BeforeEach
    void setUp() {
        sslDriver = new SslDriver();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void initialize() throws Exception {
        sslDriver.initialize();
        await().atMost(5, TimeUnit.SECONDS)
                .until(sslDriver::isOpen);
    }
}
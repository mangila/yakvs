package com.github.mangila.yakvs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

class DriverTest {

    private Driver driver;

    @BeforeEach
    void setUp() {
        driver = new Driver();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test() {
        driver.initialize();
        await()
                .atMost(5, TimeUnit.SECONDS)
                .until(driver::isOpen);
    }
}
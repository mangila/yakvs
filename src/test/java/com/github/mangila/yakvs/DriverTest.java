package com.github.mangila.yakvs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class DriverTest {

    private Driver driver;

    @BeforeEach
    void setUp() {
        driver = new Driver();
    }

    @AfterEach
    void tearDown() {
        driver.close();
    }

    @Test
    void test() throws IOException {
        driver.initialize();
        await().atMost(5, TimeUnit.SECONDS)
                .until(() -> driver.getServer().isOpen());
        try (var socket = new Socket("localhost", 11866);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("""
                    SET
                    myKey
                    myValue
                    """);
            assertThat(in.readLine()).isEqualTo("OK");
        }

        try (var socket = new Socket("localhost", 11866);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("""
                    GET
                    myKey
                    """);
            assertThat(in.readLine()).isEqualTo("myValue");
        }

        try (var socket = new Socket("localhost", 11866);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("SAVE");
            assertThat(in.readLine()).isEqualTo("OK");
        }

        try (var socket = new Socket("localhost", 11866);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("KEYS");
            System.out.println(in.lines().collect(Collectors.joining(System.lineSeparator())));
        }

        try (var socket = new Socket("localhost", 11866);
             var out = new PrintWriter(socket.getOutputStream(), true);
             var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("FLUSH");
            assertThat(in.readLine()).isEqualTo("OK");
        }
    }
}
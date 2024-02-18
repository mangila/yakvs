package com.github.mangila.yakvs;

import com.github.mangila.yakvs.common.ServerConfig;
import com.github.mangila.yakvs.server.Server;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;

public class Driver {

    public static void main(String[] args) {
        try {
            ServerConfig serverConfig;
            try (var resources = Driver.class.getClassLoader().getResourceAsStream("server.yml")) {
                var yaml = new Yaml(new Constructor(ServerConfig.class, new LoaderOptions()));
                serverConfig = yaml.load(resources);
            }
            var server = new Server(serverConfig.getPort(), serverConfig.getBufferSize());
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

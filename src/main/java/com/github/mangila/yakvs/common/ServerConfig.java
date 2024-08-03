package com.github.mangila.yakvs.common;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;

@lombok.Getter
@lombok.Setter
public class ServerConfig {

    private String name;
    private int port;
    private boolean quickstart;

    public static ServerConfig load(String yml) {
        try (var resources = ServerConfig.class.getClassLoader().getResourceAsStream(yml)) {
            var yaml = new Yaml(new Constructor(ServerConfig.class, new LoaderOptions()));
            return yaml.load(resources);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
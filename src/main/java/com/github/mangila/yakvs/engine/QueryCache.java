package com.github.mangila.yakvs.engine;

import java.util.HashMap;
import java.util.Map;

public class QueryCache {

    private static final Map<String, Query> KNOWN_QUERIES = new HashMap<>();

    public static Query get(String request) {
        return KNOWN_QUERIES.get(request);
    }

    public static boolean hasQuery(String request) {
        return KNOWN_QUERIES.containsKey(request);
    }

    public static Query put(String request) {
        var split = request.split("\\s");
        var keyword = Keyword.valueOf(split[0]);
        var query = switch (split.length) {
            case 1 -> Query.builder()
                    .keyword(keyword)
                    .rawQuery(request)
                    .build();
            case 2 -> Query.builder()
                    .keyword(keyword)
                    .key(Key.builder()
                            .key(split[1])
                            .build()
                    )
                    .rawQuery(request)
                    .build();
            case 3 -> Query.builder()
                    .keyword(keyword)
                    .key(Key.builder()
                            .key(split[1])
                            .build()
                    )
                    .value(Value.builder()
                            .value(split[2].getBytes())
                            .build()
                    )
                    .rawQuery(request)
                    .build();
            default -> throw new IllegalStateException("Unexpected value: " + split.length);
        };
        KNOWN_QUERIES.put(request, query);
        return query;
    }
}

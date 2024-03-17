package com.github.mangila.yakvs.engine;

import com.github.mangila.yakvs.engine.query.Keyword;
import com.github.mangila.yakvs.engine.query.Query;

import java.util.*;
import java.util.regex.Pattern;

public class Parser {

    private static final List<Pattern> PATTERNS = List.of(
            Pattern.compile("^SET \\S+ \\S+\\z"),
            Pattern.compile("^(GET|DELETE) \\S+\\z"),
            Pattern.compile("^(COUNT|FLUSH|DUMP|SAVE)\\z")
    );

    public static final Map<String, Query> KNOWN_QUERIES = new HashMap<>();

    static {
        KNOWN_QUERIES.put(Keyword.COUNT.toString(), new Query(Keyword.COUNT));
        KNOWN_QUERIES.put(Keyword.FLUSH.toString(), new Query(Keyword.FLUSH));
        KNOWN_QUERIES.put(Keyword.DUMP.toString(), new Query(Keyword.DUMP));
        KNOWN_QUERIES.put(Keyword.SAVE.toString(), new Query(Keyword.SAVE));
    }

    public Optional<Query> parse(String request) {
        if (Objects.isNull(request) || request.isBlank()) {
            return Optional.empty();
        }
        if (KNOWN_QUERIES.containsKey(request)) {
            return Optional.of(KNOWN_QUERIES.get(request));
        }
        for (var pattern : PATTERNS) {
            if (pattern.matcher(request).matches()) {
                var query = Query.toQuery(request);
                KNOWN_QUERIES.put(request, query);
                return Optional.of(query);
            }
        }
        return Optional.empty();
    }
}

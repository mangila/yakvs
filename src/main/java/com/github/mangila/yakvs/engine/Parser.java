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
        KNOWN_QUERIES.put("COUNT", new Query(Keyword.COUNT));
        KNOWN_QUERIES.put("FLUSH", new Query(Keyword.FLUSH));
        KNOWN_QUERIES.put("DUMP", new Query(Keyword.DUMP));
        KNOWN_QUERIES.put("SAVE", new Query(Keyword.SAVE));
    }

    public Optional<Query> parse(String query) {
        if (Objects.isNull(query) || query.isBlank()) {
            return Optional.empty();
        }
        if (KNOWN_QUERIES.containsKey(query)) {
            return Optional.of(KNOWN_QUERIES.get(query));
        }
        for (var pattern : PATTERNS) {
            if (pattern.matcher(query).matches()) {
                var q = Query.toQuery(query);
                KNOWN_QUERIES.put(query, q);
                return Optional.of(q);
            }
        }
        return Optional.empty();
    }
}

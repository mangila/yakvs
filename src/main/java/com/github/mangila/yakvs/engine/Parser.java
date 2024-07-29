package com.github.mangila.yakvs.engine;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class Parser {

    private static final List<Pattern> PATTERNS = List.of(
            Pattern.compile("^SET \\S+ \\S+\\z"),
            Pattern.compile("^(GET|DELETE) \\S+\\z"),
            Pattern.compile("^(COUNT|FLUSH|DUMP|SAVE)\\z")
    );

    public Optional<Query> parse(String request) {
        if (Objects.isNull(request) || request.isBlank()) {
            return Optional.empty();
        }
        if (QueryCache.hasQuery(request)) {
            return Optional.of(QueryCache.get(request));
        }
        for (var pattern : PATTERNS) {
            if (pattern.matcher(request).matches()) {
                return Optional.of(QueryCache.put(request));
            }
        }
        return Optional.empty();
    }
}

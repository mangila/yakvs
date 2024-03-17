package com.github.mangila.yakvs.engine.query;

import com.github.mangila.yakvs.engine.Key;
import com.github.mangila.yakvs.engine.Value;

public record Query(Keyword keyword, Key key, Value value) {

    public Query(Keyword keyword) {
        this(keyword, null, null);
    }

    public Query(Keyword keyword, Key key) {
        this(keyword, key, null);
    }

    public static Query toQuery(String query) {
        var split = query.split("\\s");
        if (split.length == 2) {
            return new Query(Keyword.valueOf(split[0]), new Key(split[1]));
        } else if (split.length == 3) {
            return new Query(Keyword.valueOf(split[0]), new Key(split[1]), new Value(split[2]));
        }
        return new Query(Keyword.valueOf(split[0]));
    }
}

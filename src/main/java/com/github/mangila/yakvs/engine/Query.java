package com.github.mangila.yakvs.engine;

@lombok.Builder(toBuilder = true)
@lombok.Getter
public class Query {
    private Keyword keyword;
    private Key key;
    private Value value;
    private String rawQuery;
}

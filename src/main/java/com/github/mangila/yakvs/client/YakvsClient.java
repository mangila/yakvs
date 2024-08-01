package com.github.mangila.yakvs.client;

import com.github.mangila.proto.Entry;
import com.github.mangila.proto.Query;
import com.github.mangila.proto.Response;

public interface YakvsClient {

    Response get(Entry entry);

    Response set(Entry entry);

    Response delete(Entry entry);

    Response count();

    Response keys();

    Response flush();

    Response save();

    Response execute(Query query);
}

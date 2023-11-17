package org.openobservatory.ooniprobe.factory;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ResponseFactory {

    public static Response successWithValue(String value, String fromUrl) {
        return new Response.Builder()
                .body(ResponseBody.create(value, MediaType.parse("application/json; charset=utf-8")))
                .code(200)
                .request(new Request.Builder().url(fromUrl).build())
                .protocol(Protocol.HTTP_2)
                .message("")
                .build();
    }
}

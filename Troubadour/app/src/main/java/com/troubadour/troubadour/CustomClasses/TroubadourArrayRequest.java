package com.troubadour.troubadour.CustomClasses;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by marcus on 3/22/17.
 */

public class TroubadourArrayRequest extends JsonArrayRequest {
    private Map<String, String> tHeaders = new ArrayMap<>();

    public TroubadourArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public TroubadourArrayRequest(int method, String url, JSONArray jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() {
        return tHeaders;
    }

    public TroubadourArrayRequest setHeader(String k, String v) {
        tHeaders.put(k, v);
        return this;
    }
}

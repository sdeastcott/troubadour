package com.troubadour.troubadour.CustomClasses;

import android.provider.Settings;
import android.support.v4.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by marcus on 3/22/17.
 */

public class TroubadourObjectRequest extends JsonObjectRequest {
    private Map<String, String> tHeaders = new ArrayMap<>();
    public TroubadourObjectRequest(int method, String url, JSONObject jsonRequest,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public TroubadourObjectRequest(String url, JSONObject jsonRequest,
                                   Response.Listener<JSONObject> listener,
                                   Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() {
        return tHeaders;
    }

    public TroubadourObjectRequest setHeader(String k, String v) {
        tHeaders.put(k, v);
        return this;
    }
}

package com.troubadour.troubadour.CustomClasses;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by marcus on 3/22/17.
 */

public class TroubadourArrayRequest extends JsonArrayRequest {
    private Map<String, String> tHeaders = new ArrayMap<>();
    private String mBody;

    public TroubadourArrayRequest(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public TroubadourArrayRequest(int method, String url, JSONArray jsonRequest,
                                  Response.Listener<JSONArray> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public TroubadourArrayRequest(int method, String url, JSONObject jsonRequest,
                                  Response.Listener<JSONArray> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        mBody = (jsonRequest == null) ? null : jsonRequest.toString();
    }

    @Override
    public Map<String, String> getHeaders() {
        return tHeaders;
    }

    public TroubadourArrayRequest setHeader(String k, String v) {
        tHeaders.put(k, v);
        return this;
    }

    @Override
    public byte[] getBody() { // Hack to allow JsonObject body for JsonArray Request
        if(mBody != null) {
            try {
                return mBody.getBytes(PROTOCOL_CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return super.getBody();
    }
}

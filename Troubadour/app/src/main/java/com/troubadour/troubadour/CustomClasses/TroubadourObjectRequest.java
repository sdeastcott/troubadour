package com.troubadour.troubadour.CustomClasses;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.troubadour.troubadour.CustomClasses.TroubadourRequestError;
import com.troubadour.troubadour.CustomClasses.TroubadourRequestErrorHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by marcus on 3/22/17.
 */

public class TroubadourObjectRequest extends JsonObjectRequest {
    private Map<String, String> tHeaders = new ArrayMap<>();
    private String mBody;

    public TroubadourObjectRequest(int method, String url,
                                   Response.Listener<JSONObject> listener,
                                   TroubadourRequestErrorHandler errorListener) {
        super(method, url, null, listener, (VolleyError e) -> errorListener.onError(new TroubadourRequestError(e)));
    }

    public TroubadourObjectRequest(int method, String url, JSONObject jsonRequest,
                                   Response.Listener<JSONObject> listener,
                                   TroubadourRequestErrorHandler errorListener) {
        super(method, url, jsonRequest, listener, (VolleyError e) -> errorListener.onError(new TroubadourRequestError(e)));
    }

    public TroubadourObjectRequest(String url, JSONObject jsonRequest,
                                   Response.Listener<JSONObject> listener,
                                   TroubadourRequestErrorHandler errorListener) {
        super(url, jsonRequest, listener, (VolleyError e) -> errorListener.onError(new TroubadourRequestError(e)));
    }

    public TroubadourObjectRequest(int method, String url, JSONArray jsonRequest,
                                   Response.Listener<JSONObject> listener,
                                   TroubadourRequestErrorHandler errorListener) {
        super(method, url, null, listener, (VolleyError e) -> errorListener.onError(new TroubadourRequestError(e)));
        mBody = (jsonRequest == null) ? null : jsonRequest.toString();
    }


    @Override
    public Map<String, String> getHeaders() {
        return tHeaders;
    }

    public TroubadourObjectRequest setHeader(String k, String v) {
        tHeaders.put(k, v);
        return this;
    }

    @Override
    public byte[] getBody() { // Hack to allow JsonArray body for JsonObject Request
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

package com.troubadour.troubadour.CustomClasses;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by marcus on 3/24/17.
 */

public class TroubadourRequestError extends Exception {
    private VolleyError innerError;
    public TroubadourRequestError(VolleyError e){
        super(e.getMessage());
        innerError = e;
    }

    public int getStatus(){
        return innerError.networkResponse.statusCode;
    }

    public JSONObject getResponseObject() throws JSONException {
        return new JSONObject(new String(innerError.networkResponse.data));
    }

    public Map<String, String> getResponseHeaders(){
        return innerError.networkResponse.headers;
    }

    public VolleyError getVolleyError(){
        return innerError;
    }
}

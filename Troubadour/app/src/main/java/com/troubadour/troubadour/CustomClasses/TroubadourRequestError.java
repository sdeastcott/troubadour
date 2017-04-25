package com.troubadour.troubadour.CustomClasses;

import android.util.Log;

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
        if (innerError.networkResponse != null) {
            if(innerError.networkResponse.headers != null) Log.e("Troub NetError Headers:", innerError.networkResponse.headers.toString());
            if(innerError.networkResponse.data != null) Log.e("Troub NetError Data:", innerError.networkResponse.data.toString());
            Log.e("Troub NetError Status", String.valueOf(innerError.networkResponse.statusCode));
        }
    }

    public int getStatus(){
        return innerError.networkResponse.statusCode;
    }

    public JSONObject getResponseObject() throws JSONException {
        Log.e("Troub NetError Headers:", innerError.networkResponse.headers.toString());
        Log.e("Troub NetError Data:", innerError.networkResponse.data.toString());
        Log.e("Troub NetError Status", String.valueOf(innerError.networkResponse.statusCode));
        innerError.printStackTrace();
        return new JSONObject(new String(innerError.networkResponse.data));
    }

    public Map<String, String> getResponseHeaders(){
        return innerError.networkResponse.headers;
    }

    public VolleyError getVolleyError(){
        return innerError;
    }
}

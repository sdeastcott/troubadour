package com.troubadour.troubadour.CustomClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;

/**
 * Created by BBKiel on 3/6/2017.
 */

public class APIHandler {

    private static APIHandler mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    SpotifyApi api;
    String apiURL = "https://api.troubadour.tk";

    public APIHandler(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache(){
            private final LruCache<String,Bitmap>
                cache = new LruCache<String,Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url){
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url,Bitmap bitmap){
                cache.put(url,bitmap);
            }
        });

        api = new SpotifyApi();
    }

    public static synchronized APIHandler getmInstance(Context context){
        if (mInstance == null){
            mInstance = new APIHandler(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getmImageLoader(){
        return mImageLoader;
    }

    public void getPreferences(final Response.Listener<JSONObject> callback) {
         getPreferences(callback, (VolleyError e) ->
                Toast.makeText(mCtx, "Error: " + e, Toast.LENGTH_LONG));
    }

    /*Troubadour API Methods*/
    public void getPreferences(final Response.Listener<JSONObject> callback,
                               final Response.ErrorListener errHandler){
        TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(Request.Method.GET,
                apiURL + "/preferences",
                null, callback, errHandler
        );
        String android_id = Settings.Secure.getString(mCtx.getContentResolver(),
            Settings.Secure.ANDROID_ID);
        jsonObjectRequest
                .setHeader("X-USER-ID", android_id)
                .setHeader("Content-Type", "application/json");
        mRequestQueue.add(jsonObjectRequest);
    }

    public void putPreferences(JSONObject selectedPreference,
                               final Response.Listener<JSONObject> callback) {
        putPreferences(selectedPreference, callback, (VolleyError e) ->
                Toast.makeText(mCtx, "Error: " + e, Toast.LENGTH_LONG));
    }


    public void putPreferences(JSONObject selectedPreference,
                               final Response.Listener<JSONObject> callback,
                               final Response.ErrorListener errHandler) {
        TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(Request.Method.PUT,
                apiURL + "/preferences", selectedPreference, callback, errHandler
        );
        String android_id = Settings.Secure.getString(mCtx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        jsonObjectRequest
                .setHeader("X-USER-ID", android_id)
                .setHeader("Content-Type", "application/json");
        mRequestQueue.add(jsonObjectRequest);
    }

}

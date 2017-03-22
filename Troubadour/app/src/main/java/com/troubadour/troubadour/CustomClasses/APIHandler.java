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

    /*Troubadour API Methods*/
    public void getPreferences(final APICallback callback, JSONObject body){

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiURL + "/preferences", null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    callback.onSuccess(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mCtx, "Error: " + error, Toast.LENGTH_LONG);
                }

            }
            ) {
                @Override
                public Map<String,String> getHeaders(){
                    String android_id = Settings.Secure.getString(mCtx.getContentResolver(), Settings.Secure.ANDROID_ID);
                    Map<String,String> params = new ArrayMap<String,String>();
                    params.put("X-USER-ID", android_id);
                    params.put("Content-Type", "application/json");
                    return params;
                }

            };
            mRequestQueue.add(jsonObjectRequest);
    }

    public void postPreferences(final APICallback callback, JSONObject selectedPreference){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiURL + "/preferences", selectedPreference, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mCtx, "Error: " + error, Toast.LENGTH_LONG);
            }

        }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                String android_id = Settings.Secure.getString(mCtx.getContentResolver(), Settings.Secure.ANDROID_ID);
                Map<String, String> params = new ArrayMap<String, String>();
                params.put("X-USER-ID", android_id);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        mRequestQueue.add(jsonObjectRequest);
    }

    /*Troubadour API Methods APIHandlerCallback Interfaces*/
    //This allows for a function to be performed after the Response is received
    public interface APICallback{
        void onSuccess(JSONObject response);
    }

}

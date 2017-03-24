package com.troubadour.troubadour.CustomClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.util.Log;
import android.util.LruCache;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kaaes.spotify.webapi.android.SpotifyApi;

/**
 * APIHandler is a central class that handles request to the Troubadour Server
 * Created by Blair Kiel on 3/6/2017.
 */

public class APIHandler {

    //private SpotifyApi api;
    private String androidID;
    private static APIHandler mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;
    private String apiURL = "https://api.troubadour.tk";

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
        androidID = Settings.Secure.getString(mCtx.getContentResolver(), Settings.Secure.ANDROID_ID);
        //api = new SpotifyApi();
    }

    @SuppressWarnings("unused")
    public static synchronized APIHandler getmInstance(Context context){
        if (mInstance == null){
            mInstance = new APIHandler(context);
        }
        return mInstance;
    }

    @SuppressWarnings("WeakerAccess")
    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    @SuppressWarnings("unused")
    public ImageLoader getmImageLoader(){
        return mImageLoader;
    }

    /*Begin Troubadour API Methods*/

    /* getPreferences Error Handler */
    public void getPreferences(final Response.Listener<JSONObject> callback) {
         getPreferences(callback, (TroubadourRequestError e) ->
                Toast.makeText(mCtx, "Error: " + e, Toast.LENGTH_LONG).show());
    }


    /* GET /Preferences for the Troubadour API with the androidID */
    @SuppressWarnings("WeakerAccess")
    public void getPreferences(final Response.Listener<JSONObject> callback,
                               final TroubadourRequestErrorHandler errHandler){
        TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(
                Request.Method.GET,
                apiURL + "/preferences", callback, errHandler
        );

        jsonObjectRequest
                .setHeader("X-USER-ID", androidID)
                .setHeader("Content-Type", "application/json");
        mRequestQueue.add(jsonObjectRequest);
    }

    /* putPreferences Error Handler */
    public void putPreferences(JSONArray selectedPreference,
                               final Response.Listener<JSONObject> callback) {
        putPreferences(selectedPreference, callback, (TroubadourRequestError e) ->
                Toast.makeText(mCtx, "Error: " + e, Toast.LENGTH_LONG).show());
    }


    /* PUT /Preferences for the Troubadour API with the androidID and a JSONObject selectedPreference */
    @SuppressWarnings("WeakerAccess")
    public void putPreferences(JSONArray selectedPreference,
                               final Response.Listener<JSONObject> callback,
                               final TroubadourRequestErrorHandler errHandler) {
        TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(Request.Method.PUT,
                apiURL + "/preferences", selectedPreference, callback, errHandler
        );
        Log.e("androidID",androidID);
        Log.e("Put body:", selectedPreference.toString());
        jsonObjectRequest
                .setHeader("X-USER-ID", androidID)
                .setHeader("Content-Type", "application/json");
        Log.e("JSONRequestObject PUT: ",jsonObjectRequest.toString());
        mRequestQueue.add(jsonObjectRequest);
    }

    /* deletePreferences Error Handler */
    public void deletePreferences(String pref,
                                  final Response.Listener<JSONObject> callback){
        deletePreferences(pref, callback, (TroubadourRequestError e) ->
                Toast.makeText(mCtx,"Error: " + e, Toast.LENGTH_LONG).show());
    }

    /* DELETE /Preferences for the Troubadour API with the androidID and an Array of Spotify URI strings */
    @SuppressWarnings("WeakerAccess")
    public void deletePreferences(String pref,
                                  final Response.Listener<JSONObject> callback,
                                  final TroubadourRequestErrorHandler errHandler) {
           TroubadourObjectRequest jsonArrayRequest = new TroubadourObjectRequest(Request.Method.DELETE,
                    apiURL + "/preferences?ids=" + pref, callback, errHandler
            );


            jsonArrayRequest
                    .setHeader("X-USER-ID", androidID)
                    .setHeader("Content-Type", "application/json");
            Log.e("Request DELETE: ", jsonArrayRequest.toString());
            mRequestQueue.add(jsonArrayRequest);
    }



    /*  GET /Search Error Handler */
    public void getSearch(String searchQuery,
                          final Response.Listener<JSONObject> callback){
        getSearch(searchQuery, callback, (TroubadourRequestError e) ->
            Toast.makeText(mCtx, "Error: " + e, Toast.LENGTH_LONG).show());
    }

    /* GET /Search for the Troubadour API with the androidID and a search string */
    @SuppressWarnings("WeakerAccess")
    public void getSearch(String searchQuery,
                          final Response.Listener<JSONObject> callback,
                          final TroubadourRequestErrorHandler errHandler){
        TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(Request.Method.GET,
                apiURL + "/search?q=" + searchQuery, callback, errHandler
        );
       jsonObjectRequest
               .setHeader("X-USER-ID", androidID)
               .setHeader("Content-Type","application/json");
        mRequestQueue.add(jsonObjectRequest);

    }
}

package com.troubadour.troubadour.CustomClasses;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * APIHandler is a central class that handles request to the Troubadour Server
 * Created by Blair Kiel on 3/6/2017.
 */

public class APIHandler {

    private SpotifyApi spotifyApi;
    private String androidID;
    private static APIHandler mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;
    private String apiURL = "https://api.troubadour.tk";
    private static Activity callingActivity;
    private static final int canUseLocation = 1;
    private TroubadourLocationManager troubadourLocationManager;

    public APIHandler(Context context){
        mCtx = context;
        spotifyApi = new SpotifyApi();
        troubadourLocationManager = new TroubadourLocationManager(mCtx);
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
        //spotifyApi = new SpotifyApi();
    }

    public APIHandler(Activity activity, Context context){
        callingActivity = activity;
        mCtx = context;
        spotifyApi = new SpotifyApi();
        troubadourLocationManager = new TroubadourLocationManager(mCtx);
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
        //spotifyApi = new SpotifyApi();
    }



    @SuppressWarnings("unused")
    public static synchronized APIHandler getmInstance(Activity activity, Context context){
        if (mInstance == null){
            mInstance = new APIHandler(activity, context);
        }
        return mInstance;
    }


    private RequestQueue getRequestQueue(){
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
        getPreferences(callback, (TroubadourRequestError e) -> APIErrorHandler(e));
    }


    /* GET /Preferences for the Troubadour API with the androidID */
    private void getPreferences(final Response.Listener<JSONObject> callback,
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
        putPreferences(selectedPreference, callback, (TroubadourRequestError e) -> APIErrorHandler(e));
    }


    /* PUT /Preferences for the Troubadour API with the androidID and a JSONObject selectedPreference */
    private void putPreferences(JSONArray selectedPreference,
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
        deletePreferences(pref, callback, (TroubadourRequestError e) -> APIErrorHandler(e));
    }

    /* DELETE /Preferences for the Troubadour API with the androidID and an Array of Spotify URI strings */
    private void deletePreferences(String pref,
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
        getSearch(searchQuery, callback, (TroubadourRequestError e) -> APIErrorHandler(e));
    }

    /* GET /Search for the Troubadour API with the androidID and a search string */
    private void getSearch(String searchQuery,
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

    private void APIErrorHandler(TroubadourRequestError e){
        Toast.makeText(mCtx, "Network Error", Toast.LENGTH_LONG).show();
        Log.e("TroubadourRequestError", e.toString());
    }


    public void putLocation(JSONObject json) {
        TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(Request.Method.PUT,
                apiURL + "/location", json, (JSONObject obj) -> {
            Log.e("Location response", obj.toString());
        }, (TroubadourRequestError e) -> APIErrorHandler(e)
        );
        jsonObjectRequest
                .setHeader("X-USER-ID", androidID)
                .setHeader("Content-Type","application/json");
        mRequestQueue.add(jsonObjectRequest);
    }

    public void getNearby(String radius,
                          final Response.Listener<JSONObject> callback){
        try {
            TroubadourLocationObject locationObject = troubadourLocationManager.getLocation();
            String lat = locationObject.getLatitude().toString();
            String lon = locationObject.getLongitude().toString();
            getNearby(lat, lon, radius, callback, (TroubadourRequestError e) -> APIErrorHandler(e));
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void getNearby(String lat, String lon, String radius,
                          final Response.Listener<JSONObject> callback,
                          final TroubadourRequestErrorHandler errorHandler) {
        TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(Request.Method.GET,
                apiURL + "/nearby?lat=" + lat + "&long=" + lon + "&radius=" + radius, callback, errorHandler
        );
        jsonObjectRequest
                .setHeader("X-USER-ID", androidID)
                .setHeader("Content-Type", "application/json");
        mRequestQueue.add(jsonObjectRequest);
    }

    /* putPreferences Error Handler */
    public void postPlaylist(String lat, String lon, String radius, String selectedPreferences[], String apiToken,
                               final Response.Listener<JSONObject> callback) {
        postPlaylist(lat, lon, radius, selectedPreferences, apiToken, callback, (TroubadourRequestError e) -> APIErrorHandler(e));
    }


    /* PUT /Preferences for the Troubadour API with the androidID and a JSONObject selectedPreference */
    private void postPlaylist(String lat, String lon, String radius, String selectedPreferences[], String apiToken,
                               final Response.Listener<JSONObject> callback,
                               final TroubadourRequestErrorHandler errHandler) {
        try {
            JSONObject mBody = new JSONObject();
            Double dLat = Double.parseDouble(lat);
            Double dLon = Double.parseDouble(lon);
            Double dRadius = Double.parseDouble(radius);
            mBody.put("lat",dLat);
            mBody.put("long",dLon);
            mBody.put("radius",dRadius);

            TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(Request.Method.POST,
                    apiURL + "/playlist", mBody, callback, errHandler
            );

        Log.e("androidID",androidID);
        Log.e("Put body:", mBody.toString());
        jsonObjectRequest
                .setHeader("X-USER-ID", androidID)
                .setHeader("X-API-KEY", apiToken)
                .setHeader("Content-Type", "application/json");
        Log.e("JSONRequestObject PUT: ",jsonObjectRequest.toString());
        Log.e("PUT AndroidID: ",androidID);
        Log.e("PUT APIToken: ",apiToken);
        mRequestQueue.add(jsonObjectRequest);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

     /* deletePreferences Error Handler */
    public void deleteBlacklistPreferences(String prefs,
                                  final Response.Listener<JSONObject> callback){
        deleteBlacklistPreferences(prefs, callback, (TroubadourRequestError e) -> APIErrorHandler(e));
    }

    /* DELETE /Preferences for the Troubadour API with the androidID and an Array of Spotify URI strings */
    private void deleteBlacklistPreferences(String prefs,
                                  final Response.Listener<JSONObject> callback,
                                  final TroubadourRequestErrorHandler errHandler) {
        TroubadourObjectRequest jsonArrayRequest = new TroubadourObjectRequest(Request.Method.DELETE,
                apiURL + "/user/blacklist?ids=" + prefs, callback, errHandler
        );
        jsonArrayRequest
                .setHeader("X-USER-ID", androidID)
                .setHeader("Content-Type", "application/json");
        Log.e("Request DELETE: ", jsonArrayRequest.toString());
        mRequestQueue.add(jsonArrayRequest);
    }

     /* getBlacklistPreferences Error Handler */
    public void getBlacklistPreferences(final Response.Listener<JSONObject> callback){
        getBlacklistPreferences(callback, (TroubadourRequestError e) -> APIErrorHandler(e));
    }

    /* /getBlacklistPreferences for the Troubadour API with the androidID*/
    private void getBlacklistPreferences(final Response.Listener<JSONObject> callback, final TroubadourRequestErrorHandler errHandler) {
        TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(Request.Method.GET,
                apiURL + "/user/blacklist", callback, errHandler
        );
        jsonObjectRequest
                .setHeader("X-USER-ID", androidID)
                .setHeader("Content-Type", "application/json");
        Log.e("Request GET: ", jsonObjectRequest.toString());
        mRequestQueue.add(jsonObjectRequest);
    }

    /* putBlacklistPreferences Error Handler */
    public void putBlacklistPreferences(JSONArray selectedPreference,
                               final Response.Listener<JSONObject> callback) {
        putBlacklistPreferences(selectedPreference, callback, (TroubadourRequestError e) -> APIErrorHandler(e));
    }


    /* PUT BlacklistPreferences for the Troubadour API with the androidID and a JSONObject selectedPreference */
    private void putBlacklistPreferences(JSONArray selectedPreference,
                                final Response.Listener<JSONObject> callback,
                                final TroubadourRequestErrorHandler errHandler) {
        TroubadourObjectRequest jsonObjectRequest = new TroubadourObjectRequest(Request.Method.PUT,
                apiURL + "/user/blacklist", selectedPreference, callback, errHandler
        );
        Log.e("androidID",androidID);
        Log.e("Put body:", selectedPreference.toString());
        jsonObjectRequest
                .setHeader("X-USER-ID", androidID)
                .setHeader("Content-Type", "application/json");
        Log.e("JSONRequestObject PUT: ",jsonObjectRequest.toString());
        mRequestQueue.add(jsonObjectRequest);
    }


}

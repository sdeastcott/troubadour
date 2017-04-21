package com.troubadour.troubadour.CustomClasses;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationService {


    @TargetApi(15)
    public void updateLocation(Context context) {
        TroubadourLocationManager locationManager = new TroubadourLocationManager(context);
        TroubadourLocationObject loc = locationManager.getLocation();
        if(loc != null)
            sendLocationUpdate(loc, context);
    }

    private void sendLocationUpdate(TroubadourLocationObject location, Context context) {
        APIHandler apiHandler = new APIHandler(context);
        JSONObject json = new JSONObject();
        try {
            json.put("lat", location.getLatitude());
            json.put("long", location.getLongitude());
            apiHandler.putLocation(json);
        }
        catch (JSONException ex) {}
    }

}
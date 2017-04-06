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
        if ( Build.VERSION.SDK_INT >= 15 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) return;
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            sendLocationUpdate(location, context);
        } else if (isGPSEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            sendLocationUpdate(location, context);
        }
    }

    private void sendLocationUpdate(Location location, Context context) {
        APIHandler api = new APIHandler(context);
        JSONObject json = new JSONObject();
        try {
            json.put("lat", location.getLatitude());
            json.put("long", location.getLongitude());
            api.putLocation(json);
        }
        catch (JSONException ex) {}
    }
}
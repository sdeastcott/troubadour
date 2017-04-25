package com.troubadour.troubadour.CustomClasses;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

/**
 * Created by James on 4/15/2017.
 */

public class TroubadourLocationManager implements LocationListener{

    private Context mContext;
    private Location location;
    private LocationManager locationManager;

    public TroubadourLocationManager(Context context){
        mContext = context;
        locationManager = (android.location.LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void onLocationChanged(Location mLocation){
        location = mLocation;
    }

    public LocationManager getLocationManager(){
        return locationManager;
    }

    public void onProviderDisabled(String provider){
    }

    public void onProviderEnabled(String provider){

    }

    public void onStatusChanged(String provider, int status, Bundle extras){

    }

    public TroubadourLocationObject getLocation() {
        if ( Build.VERSION.SDK_INT >= 15 &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        if (locationManager == null) return null;
        boolean isGPSEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {
            location = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);

            Double lat = location.getLatitude();
            Double lon = location.getLongitude();
            TroubadourLocationObject lObject = new TroubadourLocationObject(lat,lon);
            return lObject;
        } else if (isGPSEnabled) {
            //10 meters?

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*60*15, 10, this);
            if(locationManager != null){
                location = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);

                Double lat = location.getLatitude();
                Double lon = location.getLongitude();
                TroubadourLocationObject lObject = new TroubadourLocationObject(lat,lon);
                return lObject;
            }
        }
        return null;
    }


}

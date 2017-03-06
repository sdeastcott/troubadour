package com.troubadour.troubadour.TroubadourUser;

/**
 * Created by James on 3/4/2017.
 */

public class TroubadourLocation {

    //Need to use a location manager from: https://developer.android.com/guide/topics/location/strategies.html
    private String userLocation;


    public String getUserLoccation(){
        return userLocation;
    }

    public void setUserLocation(String location){
        userLocation = location;
    }
}

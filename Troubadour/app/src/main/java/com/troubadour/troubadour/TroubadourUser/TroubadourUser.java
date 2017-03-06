package com.troubadour.troubadour.TroubadourUser;

/**
 * Created by James on 3/4/2017.
 */

public class TroubadourUser {

    private String troubadourID;
    private TroubadourPreferences userPreferences;
    private TroubadourLocation userLocation;

    public String getTroubadourID(){
        return troubadourID;
    }

    public void setTroubadourID(String id){
        troubadourID = id;
    }

    public TroubadourPreferences getUserPreferences(){
        return userPreferences;
    }

    public void setUserPreferences(TroubadourPreferences pref){
        userPreferences = pref;
    }

    public TroubadourLocation getUserLocation(){
        return userLocation;
    }

    public void setUserLocation(TroubadourLocation location){
        userLocation = location;
    }




}

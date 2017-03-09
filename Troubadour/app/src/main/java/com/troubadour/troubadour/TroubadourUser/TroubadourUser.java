package com.troubadour.troubadour.TroubadourUser;

import com.troubadour.troubadour.CustomClasses.SpotifyObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 3/4/2017.
 */

public class TroubadourUser {

    private String troubadourID;
    private String userLocation;
    private ArrayList<SpotifyObject> userPreferencesSeeds = new ArrayList<>();

    public ArrayList<SpotifyObject> getUserPreferencesSeeds() {
        return userPreferencesSeeds;
    }

    public void setUserPreferencesSeeds(ArrayList<SpotifyObject> userPreferencesSeeds) {
        this.userPreferencesSeeds = userPreferencesSeeds;
    }

    public void addUserPreference(SpotifyObject newObject){
        userPreferencesSeeds.add(newObject);
    }

    public String getTroubadourID(){
        return troubadourID;
    }

    public void setTroubadourID(String id){
        troubadourID = id;
    }

}

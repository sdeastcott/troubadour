package com.troubadour.troubadour;

/**
 * Created by James on 3/4/2017.
 */

public class PreferenceListItem {

    private String prefName;
    private String prefSeed;

    public PreferenceListItem(String name, String seed){
        this.prefName = name;
        this.prefSeed = seed;
    }

    public void setPrefName(String name){
        prefName = name;
    }

    public String getPrefName(){
        return prefName;
    }

    public void setPrefSeed(String seed){
        prefSeed = seed;
    }

    public String getPrefSeed(){
        return prefSeed;
    }
}

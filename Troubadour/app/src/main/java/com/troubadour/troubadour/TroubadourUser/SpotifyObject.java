package com.troubadour.troubadour.TroubadourUser;

/**
 * Created by BBKiel on 3/6/2017.
 */

public class SpotifyObject {

    private String spotifyURI; //spotify:track:6rqhFgbbKwnb9MLmUQDhG6
    private String spotifyURL; //http://open.spotify.com/track/6rqhFgbbKwnb9MLmUQDhG6
    private String spotifyID;  //6rqhFgbbKwnb9MLmUQDhG6
    private String objName;

    public SpotifyObject(){
        spotifyURI = "";
        spotifyURL = "";
        spotifyID = "";
        objName = "";
    }
    public SpotifyObject(String spotURI, String name){
        this();
        spotifyURI = spotURI;
        objName = name;
    }

}

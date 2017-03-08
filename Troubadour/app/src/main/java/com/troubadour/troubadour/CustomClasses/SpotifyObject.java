package com.troubadour.troubadour.CustomClasses;

/**
 * Created by BBKiel on 3/6/2017.
 */

public class SpotifyObject {

    private String spotifyURI; //spotify:track:6rqhFgbbKwnb9MLmUQDhG6
    private String spotifyURL; //http://open.spotify.com/track/6rqhFgbbKwnb9MLmUQDhG6
    private String spotifyID;  //6rqhFgbbKwnb9MLmUQDhG6
    private String spotifyName;
    private String spotifyType;
    private String[] spotifyImages = new String[3];

    public SpotifyObject(){
        this.spotifyURI = "";
        this.spotifyURL = "";
        this.spotifyID = "";
        this.spotifyName = "";
        this.spotifyType = "";
    }
    public SpotifyObject(String spotURI, String spotURL, String spotID, String spotType, String[] spotImages, String spotName){
        this();
        this.spotifyURI = spotURI;
        this.spotifyURL = spotURL;
        this.spotifyID = spotID;
        this.spotifyType = spotType;
        this.spotifyImages = spotImages;
        this.spotifyName = spotName;
    }
    public String getSpotifyURI() {
        return spotifyURI;
    }

    public void setSpotifyURI(String spotifyURI) {
        this.spotifyURI = spotifyURI;
    }

    public String getSpotifyURL() {
        return spotifyURL;
    }

    public void setSpotifyURL(String spotifyURL) {
        this.spotifyURL = spotifyURL;
    }

    public String getSpotifyID() {
        return spotifyID;
    }

    public void setSpotifyID(String spotifyID) {
        this.spotifyID = spotifyID;
    }

    public String getSpotifyName() {
        return spotifyName;
    }

    public void setSpotifyName(String spotifyName) {
        this.spotifyName = spotifyName;
    }

    public String getSpotifyType() {
        return spotifyType;
    }

    public void setSpotifyType(String spotifyType) {
        this.spotifyType = spotifyType;
    }

    public String[] getSpotifyImages() {
        return spotifyImages;
    }

    public void setSpotifyImages(String[] spotifyImages) {
        this.spotifyImages = spotifyImages;
    }
}
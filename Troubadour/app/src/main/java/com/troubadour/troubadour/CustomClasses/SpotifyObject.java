package com.troubadour.troubadour.CustomClasses;

import java.util.ArrayList;

/**
 * Created by BBKiel on 3/6/2017.
 */

//SpotifyObject is the class that contains all needed data for an Artist,Album,Genre,Playlist and Track
public class SpotifyObject {

    private String spotifyURI; //spotify:track:6rqhFgbbKwnb9MLmUQDhG6
    private String spotifyURL; //http://open.spotify.com/track/6rqhFgbbKwnb9MLmUQDhG6
    private String spotifyID;  //6rqhFgbbKwnb9MLmUQDhG6
    private String spotifyType;
    private ArrayList<String> spotifyImages;
    private String spotifyName;
    private String spotifySecondaryArtist;


    public SpotifyObject(){
        this.spotifyURI = "";
        this.spotifyURL = "";
        this.spotifyID = "";
        this.spotifyType = "";
        this.spotifyImages = new ArrayList<>();
        this.spotifyName = "";
        this.spotifySecondaryArtist = "";
    }

    public SpotifyObject(String spotURI, String spotURL, String spotID, String spotType, ArrayList<String> spotImages, String spotName, String spotSecondaryArtist) {
        this();
        this.spotifyURI = spotURI;
        this.spotifyURL = spotURL;
        this.spotifyID = spotID;
        this.spotifyType = spotType;
        this.spotifyImages = spotImages;
        this.spotifyName = spotName;
        this.spotifySecondaryArtist = spotSecondaryArtist;
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

    public ArrayList<String> getSpotifyImages() {
        return spotifyImages;
    }

    public void setSpotifyImages(ArrayList<String> spotifyImages) {
        this.spotifyImages = spotifyImages;
    }

    public String getSpotifySecondaryArtist() {
        return spotifySecondaryArtist;
    }

    public void setSpotifySecondaryArtist(String spotifySecondaryArtist) {
        this.spotifySecondaryArtist = spotifySecondaryArtist;
    }

}


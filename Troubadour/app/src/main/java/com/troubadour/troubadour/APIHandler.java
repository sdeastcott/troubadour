package com.troubadour.troubadour;

import kaaes.spotify.webapi.android.SpotifyApi;

/**
 * Created by BBKiel on 3/6/2017.
 */

public class APIHandler {

    SpotifyApi api;

    public APIHandler(){
        api = new SpotifyApi();
    }
}

package com.troubadour.troubadour.Fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.CustomClasses.TroubadourLocationManager;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;
import com.troubadour.troubadour.CustomClasses.TroubadourLocationObject;
import com.troubadour.troubadour.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class HostFragment extends Fragment {

    private APIHandler apiHandler;
    private View fragView;
    private ListView lView;
    private Menu prefMenu;
    private MenuInflater prefMenuInflater;
    private ArrayList<SpotifyObject> nearbyListItems;
    private ArrayList<SpotifyObject> blacklistListItems;
    private ArrayList<SpotifyObject> toDeleteListItems;
    private ArrayList<String> selectedPreferenceListItems;
    private TroubadourLocationManager troubadourLocationManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private RadioGroup radioGroup;
    private RadioButton nearbyButton;
    private RadioButton blacklistButton;

    public HostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        troubadourLocationManager = new TroubadourLocationManager(getContext());
        apiHandler = new APIHandler(getContext());
        fragView = inflater.inflate(R.layout.fragment_host, container, false);

        selectedPreferenceListItems = new ArrayList<>();
        nearbyListItems = new ArrayList<>();
        blacklistListItems = new ArrayList<>();
        toDeleteListItems = new ArrayList<>();

        nearbyButton = (RadioButton) fragView.findViewById(R.id.nearbyRadioButton);
        nearbyButton.setChecked(true);
        blacklistButton = (RadioButton) fragView.findViewById(R.id.blacklistedRadioButton);

        progressBar = (ProgressBar) fragView.findViewById(R.id.hostProgressBar);

        swipeRefreshLayout = (SwipeRefreshLayout) fragView.findViewById(R.id.host_swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadListview();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        radioGroup = (RadioGroup) fragView.findViewById(R.id.radioTabs);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.nearbyRadioButton) {
                    selectedPreferenceListItems.clear();
                    nearbyButton.setTextColor(Color.parseColor("#009625"));
                    blacklistButton.setTextColor(Color.parseColor("#FFFFFF"));
                    if(nearbyListItems.size() == 0){
                        getNearbyPreferences();
                    }else{
                        noReloadRefresh();
                    }
                } else if (checkedId == R.id.blacklistedRadioButton) {
                    selectedPreferenceListItems.clear();
                    blacklistButton.setTextColor(Color.parseColor("#009625"));
                    nearbyButton.setTextColor(Color.parseColor("#FFFFFF"));
                    if(blacklistListItems.size() == 0) {
                        getBlacklistPreferences();
                    }else{
                        noReloadRefresh();
                    };
                }
            }
        });

        loadListview();


        return fragView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        prefMenu = menu;
        prefMenuInflater = menuInflater;
        prefMenuInflater.inflate(R.menu.troubadour_menu, menu);
        prefMenu.findItem(R.id.trashCanPreferenceListActionBar).setVisible(false);
        super.onCreateOptionsMenu(prefMenu,prefMenuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.trashCanPreferenceListActionBar){
            String strPref = "";
            for (String selectedPref : selectedPreferenceListItems) {
                strPref += selectedPref + ",";
            }

            strPref = strPref.substring(0,strPref.length()-1);
            removeSelectedPreferences();
            prefMenu.findItem(R.id.trashCanPreferenceListActionBar).setVisible(false);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void removeSelectedPreferences(){
        if (nearbyButton.isChecked()) {
            //Cant concurrently check if selected, delete and continue iterating
            //So it is split up
            for (String item : selectedPreferenceListItems) {
                for (SpotifyObject spotifyObject : nearbyListItems){
                    if (spotifyObject.getSpotifyURI().equals(item)){
                        toDeleteListItems.add(spotifyObject);
                    }
                }
            }
            for (SpotifyObject sObject : toDeleteListItems){
                nearbyListItems.remove(sObject);
            }
            noReloadRefresh();
        }
        //do for blacklist
        toDeleteListItems.clear();
    }

    public void loadListview(){
        if(nearbyButton.isChecked()){
            progressBar.setVisibility(View.VISIBLE);
            nearbyButton.setTextColor(Color.parseColor("#009625"));
            blacklistButton.setTextColor(Color.parseColor("#FFFFFF"));
            getNearbyPreferences();
        }else if(blacklistButton.isChecked()){
            //do later
            progressBar.setVisibility(View.VISIBLE);
            blacklistButton.setTextColor(Color.parseColor("#009625"));
            nearbyButton.setTextColor(Color.parseColor("#FFFFFF"));
            getBlacklistPreferences();
        }
    }

    public void getNearbyPreferences(){
        TroubadourLocationObject locationObject = troubadourLocationManager.getLocation();
        Double lat = locationObject.getLatitude();
        Double lon = locationObject.getLongitude();
        //Get Radius from pref later
        int radius = 500000000;
        apiHandler.getNearby(lat.toString(),lon.toString(),String.valueOf(radius),this::loadNearbyPreferences);
    }

    public void getBlacklistPreferences(){
        //apiHandler
        JSONObject jsonObject = new JSONObject();
        loadBlacklistPreferences(jsonObject);
    }

    public void loadNearbyPreferences(JSONObject jsonObject){
        nearbyListItems = new ArrayList<>();
        lView = (ListView) fragView.findViewById(R.id.prefNearbyListView);

        try {
            SpotifyObject displayObject;
            String id;
            String name;
            String uri;
            String secondaryArtist = "";

            //String[] images = new String[3];
            ArrayList<String> images = new ArrayList<>();

            //Log.e("data retrieval")
            JSONObject jData = jsonObject.getJSONObject("data");
            JSONArray jArtists = jData.getJSONArray("artists");
            JSONArray jTracks = jData.getJSONArray("tracks");
            JSONArray jAlbums = jData.getJSONArray("albums");
            JSONArray jGenres = jData.getJSONArray("genres");
            JSONArray jSecondaryArtistArr;
            JSONObject jSecondaryArtistObj;

            Log.e("Length", "jArtist length is:" + jArtists.length());
            Log.e("Length", "jTracks length is:" + jTracks.length());
            Log.e("Length", "jAlbum length is:" + jAlbums.length());


            if ((jArtists.length() == 0) && (jTracks.length() == 0) && (jAlbums.length() == 0)) {
                displayObject = new SpotifyObject("", "", "", "display", null, "No Music Preferences", "");
                nearbyListItems.add(displayObject);
            }
            else{

                if (jArtists.length() > 0) {
                    displayObject = new SpotifyObject("", "", "", "display", null, "Artists", "");
                    nearbyListItems.add(displayObject);
                }

                //Artists
                for (int i = 0; i < jArtists.length(); i++) {
                    JSONObject pref = jArtists.getJSONObject(i);
                    String type = pref.getString("type");
                    id = pref.getString("spotify_id");
                    name = pref.getString("name");
                    JSONArray tempArr = pref.getJSONArray("images");
                    if (tempArr.length() > 0) {
                        for (int j = 0; j < tempArr.length(); j++) {
                            images.add(tempArr.getJSONObject(j).getString("url"));
                        }
                    }
                    uri = pref.getString("uri");

                    SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name, "");
                    nearbyListItems.add(spotObject);
                    images = new ArrayList<>();
                }

                //Genres
                if (jGenres.length() > 0){
                    displayObject = new SpotifyObject("","","","display",null,"Genres","");
                    nearbyListItems.add(displayObject);
                }

                for (int i = 0; i < jGenres.length(); i++){
                    JSONObject pref = jGenres.getJSONObject(i);
                    String type = pref.getString("type");
                    id = pref.getString("spotify_id");
                    name = pref.getString("name");
                    uri = pref.getString("uri");

                /*&unimplemented artwork
                JSONArray tempArr = pref.getJSONArray("images");
                if(tempArr.length() > 0){
                    for (int j = 0; i < 3; j++){
                        images[j] = tempArr.getJSONObject(j).getString("url");
                    }
                }
                */

                    SpotifyObject spotObject = new SpotifyObject(uri,"",id,type,null,name,"");
                    nearbyListItems.add(spotObject);
                }


                if (jTracks.length() > 0) {
                    displayObject = new SpotifyObject("", "", "", "display", null, "Tracks", "");
                    nearbyListItems.add(displayObject);
                }
                //Tracks
                for (int i = 0; i < jTracks.length(); i++) {
                    JSONObject pref = jTracks.getJSONObject(i);
                    String type = pref.getString("type");
                    id = pref.getString("spotify_id");
                    name = pref.getString("name");
                    uri = pref.getString("uri");
                    jSecondaryArtistArr = pref.getJSONArray("artists");
                    for (int j = 0; j < jSecondaryArtistArr.length(); j++){
                        if(j != 0){ secondaryArtist += ", ";}
                        jSecondaryArtistObj = jSecondaryArtistArr.getJSONObject(j);
                        secondaryArtist += jSecondaryArtistObj.getString("name");
                    }

                    SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name, secondaryArtist);
                    nearbyListItems.add(spotObject);
                    secondaryArtist = "";
                }

                if (jAlbums.length() > 0) {
                    displayObject = new SpotifyObject("", "", "", "display", null, "Albums", "");
                    nearbyListItems.add(displayObject);
                }

                //Albums
                for (int i = 0; i < jAlbums.length(); i++) {
                    JSONObject pref = jAlbums.getJSONObject(i);
                    String type = pref.getString("type");
                    id = pref.getString("spotify_id");
                    name = pref.getString("name");
                    JSONArray tempArr = pref.getJSONArray("images");
                    if (tempArr.length() > 0) {
                        for (int j = 0; j < tempArr.length(); j++) {
                            images.add(tempArr.getJSONObject(j).getString("url"));
                        }
                    }
                    uri = pref.getString("uri");
                    jSecondaryArtistArr = pref.getJSONArray("artists");
                    for (int j = 0; j < jSecondaryArtistArr.length(); j++){
                        if(j != 0){ secondaryArtist += ", ";}
                        jSecondaryArtistObj = jSecondaryArtistArr.getJSONObject(j);
                        secondaryArtist += jSecondaryArtistObj.getString("name");
                    }

                    SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name, secondaryArtist);
                    nearbyListItems.add(spotObject);
                    images = new ArrayList<>();
                    secondaryArtist = "";

                }
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.fragment_host, nearbyListItems);
        lView.setAdapter(lAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(nearbyButton.isChecked()) {
                    SpotifyObject selectedItem = nearbyListItems.get(position);
                    if (selectedPreferenceListItems.contains(selectedItem.getSpotifyURI())) {
                        lView.setItemChecked(position, false);
                        selectedPreferenceListItems.remove(selectedItem.getSpotifyURI());
                    } else {
                        lView.setItemChecked(position, true);
                        selectedPreferenceListItems.add(selectedItem.getSpotifyURI());
                    }

                    //If the menu item for trash is not visible
                    MenuItem item = prefMenu.findItem(R.id.trashCanPreferenceListActionBar);
                    if (selectedPreferenceListItems.isEmpty() & item.isVisible()) {
                        item.setVisible(false);
                    } else if ((!selectedPreferenceListItems.isEmpty()) & (!item.isVisible())) {
                        item.setVisible(true);
                    }
                }
                //if blacklist is checked

            }
        });
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void loadBlacklistPreferences(JSONObject jsonObject){
        lView = (ListView) fragView.findViewById(R.id.prefNearbyListView);

        try {
            SpotifyObject displayObject;
            String id;
            String name;
            String uri;
            String secondaryArtist = "";

            //String[] images = new String[3];
            ArrayList<String> images = new ArrayList<>();

            //Log.e("data retrieval")
            JSONObject jData = jsonObject.getJSONObject("data");
            JSONArray jArtists = jData.getJSONArray("artists");
            JSONArray jTracks = jData.getJSONArray("tracks");
            JSONArray jAlbums = jData.getJSONArray("albums");
            JSONArray jGenres = jData.getJSONArray("genres");
            JSONArray jSecondaryArtistArr;
            JSONObject jSecondaryArtistObj;

            Log.e("Length", "jArtist length is:" + jArtists.length());
            Log.e("Length", "jTracks length is:" + jTracks.length());
            Log.e("Length", "jAlbum length is:" + jAlbums.length());


            if ((jArtists.length() == 0) && (jTracks.length() == 0) && (jAlbums.length() == 0)) {
                displayObject = new SpotifyObject("", "", "", "display", null, "No Music Preferences", "");
                blacklistListItems.add(displayObject);
            }
            else{

                if (jArtists.length() > 0) {
                    displayObject = new SpotifyObject("", "", "", "display", null, "Artists", "");
                    blacklistListItems.add(displayObject);
                }

                //Artists
                for (int i = 0; i < jArtists.length(); i++) {
                    JSONObject pref = jArtists.getJSONObject(i);
                    String type = pref.getString("type");
                    id = pref.getString("spotify_id");
                    name = pref.getString("name");
                    JSONArray tempArr = pref.getJSONArray("images");
                    if (tempArr.length() > 0) {
                        for (int j = 0; j < tempArr.length(); j++) {
                            images.add(tempArr.getJSONObject(j).getString("url"));
                        }
                    }
                    uri = pref.getString("uri");

                    SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name, "");
                    blacklistListItems.add(spotObject);
                    images = new ArrayList<>();
                }

                //Genres
                if (jGenres.length() > 0){
                    displayObject = new SpotifyObject("","","","display",null,"Genres","");
                    blacklistListItems.add(displayObject);
                }

                for (int i = 0; i < jGenres.length(); i++){
                    JSONObject pref = jGenres.getJSONObject(i);
                    String type = pref.getString("type");
                    id = pref.getString("spotify_id");
                    name = pref.getString("name");
                    uri = pref.getString("uri");

                /*&unimplemented artwork
                JSONArray tempArr = pref.getJSONArray("images");
                if(tempArr.length() > 0){
                    for (int j = 0; i < 3; j++){
                        images[j] = tempArr.getJSONObject(j).getString("url");
                    }
                }
                */

                    SpotifyObject spotObject = new SpotifyObject(uri,"",id,type,null,name,"");
                    blacklistListItems.add(spotObject);
                }


                if (jTracks.length() > 0) {
                    displayObject = new SpotifyObject("", "", "", "display", null, "Tracks", "");
                    blacklistListItems.add(displayObject);
                }
                //Tracks
                for (int i = 0; i < jTracks.length(); i++) {
                    JSONObject pref = jTracks.getJSONObject(i);
                    String type = pref.getString("type");
                    id = pref.getString("spotify_id");
                    name = pref.getString("name");
                    uri = pref.getString("uri");
                    jSecondaryArtistArr = pref.getJSONArray("artists");
                    for (int j = 0; j < jSecondaryArtistArr.length(); j++){
                        if(j != 0){ secondaryArtist += ", ";}
                        jSecondaryArtistObj = jSecondaryArtistArr.getJSONObject(j);
                        secondaryArtist += jSecondaryArtistObj.getString("name");
                    }

                    SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name, secondaryArtist);
                    blacklistListItems.add(spotObject);
                    secondaryArtist = "";
                }

                if (jAlbums.length() > 0) {
                    displayObject = new SpotifyObject("", "", "", "display", null, "Albums", "");
                    blacklistListItems.add(displayObject);
                }

                //Albums
                for (int i = 0; i < jAlbums.length(); i++) {
                    JSONObject pref = jAlbums.getJSONObject(i);
                    String type = pref.getString("type");
                    id = pref.getString("spotify_id");
                    name = pref.getString("name");
                    JSONArray tempArr = pref.getJSONArray("images");
                    if (tempArr.length() > 0) {
                        for (int j = 0; j < tempArr.length(); j++) {
                            images.add(tempArr.getJSONObject(j).getString("url"));
                        }
                    }
                    uri = pref.getString("uri");
                    jSecondaryArtistArr = pref.getJSONArray("artists");
                    for (int j = 0; j < jSecondaryArtistArr.length(); j++){
                        if(j != 0){ secondaryArtist += ", ";}
                        jSecondaryArtistObj = jSecondaryArtistArr.getJSONObject(j);
                        secondaryArtist += jSecondaryArtistObj.getString("name");
                    }

                    SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name, secondaryArtist);
                    blacklistListItems.add(spotObject);
                    images = new ArrayList<>();
                    secondaryArtist = "";

                }
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.fragment_host, blacklistListItems);
        lView.setAdapter(lAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(nearbyButton.isChecked()) {
                    SpotifyObject selectedItem = nearbyListItems.get(position);
                    if (selectedPreferenceListItems.contains(selectedItem.getSpotifyURI())) {
                        lView.setItemChecked(position, false);
                        selectedPreferenceListItems.remove(selectedItem.getSpotifyURI());
                    } else {
                        lView.setItemChecked(position, true);
                        selectedPreferenceListItems.add(selectedItem.getSpotifyURI());
                    }

                    //If the menu item for trash is not visible
                    MenuItem item = prefMenu.findItem(R.id.trashCanPreferenceListActionBar);
                    if (selectedPreferenceListItems.isEmpty() & item.isVisible()) {
                        item.setVisible(false);
                    } else if ((!selectedPreferenceListItems.isEmpty()) & (!item.isVisible())) {
                        item.setVisible(true);
                    }
                }
                //if blacklist is checked

            }
        });
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void noReloadRefresh(){
        if (nearbyButton.isChecked()) {
            PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.fragment_host, nearbyListItems);
            lView.setAdapter(lAdapter);
        }
        else{
            PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.fragment_host, blacklistListItems);
            lView.setAdapter(lAdapter);
        }

    }


}
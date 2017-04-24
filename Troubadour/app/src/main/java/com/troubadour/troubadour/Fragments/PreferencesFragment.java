package com.troubadour.troubadour.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.troubadour.troubadour.Activities.CreatePreferenceActivity;
import com.troubadour.troubadour.Activities.HelpActivity;
import com.troubadour.troubadour.Activities.HomeActivity;
import com.troubadour.troubadour.Activities.SettingsActivity;
import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;
import com.troubadour.troubadour.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PreferencesFragment extends Fragment {

    private APIHandler apiHandler;
    private Menu prefMenu;
    private MenuInflater prefMenuInflater;
    private ListView lView;
    private View fragView;
    private ArrayList<SpotifyObject> preferenceListItems = new ArrayList<>();
    private ArrayList<String> selectedPreferenceListItems = new ArrayList<>();

    public PreferencesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        fragView = inflater.inflate(R.layout.fragment_preferences, container, false);

        FloatingActionButton fab = (FloatingActionButton) fragView.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create and Start CreatePreferenceActivity
                Intent preferenceIntent = new Intent(getActivity(), CreatePreferenceActivity.class);
                preferenceIntent.setClassName("com.troubadour.troubadour","com.troubadour.troubadour.Activities.CreatePreferenceActivity");
                getActivity().startActivity(preferenceIntent);
            }
        });
        initUI();
        apiHandler.getPreferences(this::updateListView);
        return fragView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater menuInflater){
        prefMenuInflater = menuInflater;
        prefMenu = menu;
        //prefMenuInflater.inflate(R.menu.troubadour_menu, prefMenu);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.trashCanPreferenceListActionBar) {
            String strPref = "";
            for (String selectedPref : selectedPreferenceListItems) {
                strPref += selectedPref + ",";
            }

            strPref = strPref.substring(0,strPref.length()-1);
            apiHandler.deletePreferences(strPref, this::cleanUpDelete);
        }
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        //initUI();
        apiHandler.getPreferences(this::updateListView);
        //GetUserPreferences userPreferences = new GetUserPreferences(apiURL);
        //userPreferences.execute();
    }

    public void initUI(){
        prefMenu = ((HomeActivity)getActivity()).getPrefMenu();
        preferenceListItems = new ArrayList<>();
        selectedPreferenceListItems = new ArrayList<>();
        apiHandler = new APIHandler(getActivity(),getContext());
        fragView.findViewById(R.id.preferenceProgressBar).setVisibility(View.VISIBLE);
    }

    //Populates ListView with a given jsonArray
    //Convert jsonObject to SpotifyObject then adds to a ListArray<SpotifyObject>
    //Sets Adapter to the ListArray<SpotifyObject>
    public void updateListView(JSONObject jsonObject){
        selectedPreferenceListItems.clear();
        preferenceListItems.clear();
        lView = (ListView) fragView.findViewById(R.id.preferenceListView);

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
                preferenceListItems.add(displayObject);
            }
            else{

                if (jArtists.length() > 0) {
                    displayObject = new SpotifyObject("", "", "", "display", null, "Artists", "");
                    preferenceListItems.add(displayObject);
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
                    preferenceListItems.add(spotObject);
                    images = new ArrayList<>();
                }

                //Genres
                if (jGenres.length() > 0){
                    displayObject = new SpotifyObject("","","","display",null,"Genres","");
                    preferenceListItems.add(displayObject);
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
                    preferenceListItems.add(spotObject);
                }


                if (jTracks.length() > 0) {
                    displayObject = new SpotifyObject("", "", "", "display", null, "Tracks", "");
                    preferenceListItems.add(displayObject);
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
                    preferenceListItems.add(spotObject);
                    secondaryArtist = "";
                }

                if (jAlbums.length() > 0) {
                    displayObject = new SpotifyObject("", "", "", "display", null, "Albums", "");
                    preferenceListItems.add(displayObject);
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
                    preferenceListItems.add(spotObject);
                    images = new ArrayList<>();
                    secondaryArtist = "";

                }
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.fragment_preferences, preferenceListItems);
        lView.setAdapter(lAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SpotifyObject selectedItem = preferenceListItems.get(position);
                if(!selectedItem.getSpotifyType().equals("display")) {
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
            }
        });
        fragView.findViewById(R.id.preferenceProgressBar).setVisibility(View.INVISIBLE);
    }

    public void cleanUpDelete(JSONObject jsonObject){
        selectedPreferenceListItems.clear();
        lView.clearChoices();
        MenuItem item = prefMenu.findItem(R.id.trashCanPreferenceListActionBar);
        item.setVisible(false);
    }
}

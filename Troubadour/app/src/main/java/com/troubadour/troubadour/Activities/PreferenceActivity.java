package com.troubadour.troubadour.Activities;
import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;
import com.troubadour.troubadour.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class PreferenceActivity extends AppCompatActivity {

    private APIHandler apiHandler;
    private String apiURL = "https://api.troubadour.tk";
    private Menu prefMenu;
    private MenuInflater prefMenuInflater;
    private ListView lView;
    private ArrayList<SpotifyObject> preferenceListItems = new ArrayList<>();
    private ArrayList<String> selectedPreferenceListItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Music Preferences");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create and Start CreatePreferenceActivity
                Intent preferenceIntent = new Intent(PreferenceActivity.this, CreatePreferenceActivity.class);
                preferenceIntent.setClassName("com.troubadour.troubadour","com.troubadour.troubadour.Activities.CreatePreferenceActivity");
                PreferenceActivity.this.startActivity(preferenceIntent);
            }
        });

        initUI();
    }

    public void initUI(){
        preferenceListItems = new ArrayList<>();
        apiHandler = new APIHandler(getApplicationContext());
        apiHandler.getPreferences(this::updateListView);
    }

    @Override
    public void onRestart(){
        super.onRestart();
        initUI();
        //GetUserPreferences userPreferences = new GetUserPreferences(apiURL);
        //userPreferences.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        prefMenuInflater = getMenuInflater();
        prefMenu = menu;
        prefMenuInflater.inflate(R.menu.preference_activity_trash, prefMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_settings){
           return true;
        }
        if(id == R.id.trashCanPreferenceListActionBar){
            //DeleteUserPreferences deleteUserPreferences = new DeleteUserPreferences(selectedPreferenceListItems, apiURL);
            //deleteUserPreferences.execute();
            String strPref = "";
            for (String selectedPref : selectedPreferenceListItems) {
                strPref += selectedPref + ",";
            }

            strPref = strPref.substring(0,strPref.length()-1);
            apiHandler.deletePreferences(strPref, this::cleanUpDelete);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void cleanUpDelete(JSONObject jsonObject){
        lView.clearChoices();
        MenuItem item = prefMenu.findItem(R.id.trashCanPreferenceListActionBar);
        item.setVisible(false);
        initUI();
    }

    //Populates ListView with a given jsonArray
    //Convert jsonObject to SpotifyObject then adds to a ListArray<SpotifyObject>
    //Sets Adapter to the ListArray<SpotifyObject>
    public void updateListView(JSONObject jsonObject){
        selectedPreferenceListItems = new ArrayList<>();
        lView = (ListView) findViewById(R.id.preferenceListView);

        try {
            SpotifyObject displayObject;
            String id;
            String name;
            String uri;
            String secondaryArtist = "";

            String[] images = new String[3];

            //Log.e("data retrieval")
            JSONObject jData = jsonObject.getJSONObject("data");
            JSONArray jArtists = jData.getJSONArray("artists");
            JSONArray jTracks = jData.getJSONArray("tracks");
            JSONArray jAlbums = jData.getJSONArray("albums");
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
                        for (int j = 0; j < 3; j++) {
                            images[j] = tempArr.getJSONObject(j).getString("url");
                        }
                    }
                    uri = pref.getString("uri");

                    SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name, "");
                    preferenceListItems.add(spotObject);
                    images = new String[3];
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
                        for (int j = 0; j < 3; j++) {
                            images[j] = tempArr.getJSONObject(j).getString("url");
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
                    images = new String[3];
                    secondaryArtist = "";

                }
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.content_preference, preferenceListItems);
        lView.setAdapter(lAdapter);
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SpotifyObject selectedItem = preferenceListItems.get(position);
                if(selectedPreferenceListItems.contains(selectedItem.getSpotifyURI())){
                    lView.setItemChecked(position,false);
                    selectedPreferenceListItems.remove(selectedItem.getSpotifyURI());
                }else{
                    lView.setItemChecked(position,true);
                    selectedPreferenceListItems.add(selectedItem.getSpotifyURI());
                }

                //If the menu item for trash is not visible
                MenuItem item = prefMenu.findItem(R.id.trashCanPreferenceListActionBar);
                if(selectedPreferenceListItems.isEmpty() & item.isVisible()){
                    item.setVisible(false);
                }else if((!selectedPreferenceListItems.isEmpty()) & (!item.isVisible())){
                    item.setVisible(true);
                }

            }
        });
    }
}

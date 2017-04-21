package com.troubadour.troubadour.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;
import com.troubadour.troubadour.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*CreatePreference Activity allows a user to enter new music preferences*/
public class CreateBlacklistPreferenceActivity extends AppCompatActivity {

    Context context;
    ListView prefList;
    PreferenceListAdapter preferenceListAdapter;
    private APIHandler apiHandler;
    private ArrayList<SpotifyObject> preferenceListItemArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blacklist_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Blacklist Preferences");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = this;

        //Adds the 'back' icon to the action bar
        initUI();
    }

    //If Menu item "Back" is selected returns to previous Activity
    //Standard Super methods
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Initializes UI widgets for the Activity
    public void initUI(){
        findViewById(R.id.createBlacklistPreferenceProgressBar).setVisibility(View.INVISIBLE);
        apiHandler = new APIHandler(getApplicationContext());
        //apiHandler.getPreferences(this::updateListView);

        final EditText queryEdit = (EditText) findViewById(R.id.prefSearchEditText);
        queryEdit.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event){
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    findViewById(R.id.createBlacklistPreferenceProgressBar).setVisibility(View.VISIBLE);
                    processSearch(v,queryEdit);
                    return true;
                }
                return false;
            }

        });
        /*
        Button createButton = (Button) findViewById(R.id.prefSearchButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processSearch(v,queryEdit);
            }
        });
        */
    }

    public void processSearch(View v, EditText queryEdit){
        //Dismisses Keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(queryEdit.getWindowToken(), 0);

        if(TextUtils.isEmpty(queryEdit.getText())){
            Toast.makeText(getBaseContext(), "Please enter a valid search", Toast.LENGTH_LONG).show();
        }else {
            String query = queryEdit.getText().toString();
            getSearch(query);
        }

    }

    //Get Search
    public void getSearch(String query){

        apiHandler.getSearch(query,this::updateListView);
    }

    //Populates ListView with a given jsonArray
    //Convert jsonObject to SpotifyObject then adds to a ListArray<SpotifyObject>
    //Sets Adapter to the ListArray<SpotifyObject>
    public void updateListView(JSONObject jsonObject){
        preferenceListItemArrayList = new ArrayList<>();
        prefList = (ListView) findViewById(R.id.prefSearchResultsListView);

        try {
            SpotifyObject displayObject;
            String id;
            String name;
            String uri;
            String secondaryArtist="";

            ArrayList<String> images = new ArrayList<>();

            JSONObject jData = jsonObject.getJSONObject("data");
            JSONArray jArtists = jData.getJSONArray("artists");
            JSONArray jTracks = jData.getJSONArray("tracks");
            JSONArray jAlbums = jData.getJSONArray("albums");
            JSONArray jGenres = jData.getJSONArray("genres");
            JSONObject jTopResult = jData.getJSONObject("top_result");
            JSONArray jSecondaryArtistArr;
            JSONObject jSecondaryArtistObj;

            //TopResult
            if (jTopResult.length() > 0){
                displayObject = new SpotifyObject("","","","display",null,"Top Result","");
                preferenceListItemArrayList.add(displayObject);

                String type = jTopResult.getString("type");
                id = jTopResult.getString("spotify_id");
                name = jTopResult.getString("name");
                uri = jTopResult.getString("uri");

                if(type.equals("artist")) {
                    JSONArray tempArr = jTopResult.getJSONArray("images");
                    if(tempArr.length() > 0){
                        for (int j = 0; j < tempArr.length(); j++){
                            images.add(tempArr.getJSONObject(j).getString("url"));
                        }
                    }
                   SpotifyObject spotObject = new SpotifyObject(uri,"",id,type,images,name,"");
                   preferenceListItemArrayList.add(spotObject);
                   images = new ArrayList<>();

                }else if(type.equals("album")){
                    JSONArray tempArr = jTopResult.getJSONArray("images");
                    if(tempArr.length() > 0){
                        for (int j = 0; j < tempArr.length(); j++){
                            images.add(tempArr.getJSONObject(j).getString("url"));
                        }
                    }
                    jSecondaryArtistArr = jTopResult.getJSONArray("artists");
                    for (int j = 0; j < jSecondaryArtistArr.length(); j++) {
                        if (j != 0) {
                            secondaryArtist += ", ";
                        }
                        jSecondaryArtistObj = jSecondaryArtistArr.getJSONObject(j);
                        secondaryArtist += jSecondaryArtistObj.getString("name");
                    }
                    SpotifyObject spotObject = new SpotifyObject(uri,"",id,type,images,name,secondaryArtist);
                    preferenceListItemArrayList.add(spotObject);
                    images = new ArrayList<>();
                    secondaryArtist = "";

                }else if(type.equals("track")){
                    jSecondaryArtistArr = jTopResult.getJSONArray("artists");
                    for (int j = 0; j < jSecondaryArtistArr.length(); j++) {
                        if (j != 0) {
                            secondaryArtist += ", ";
                        }
                        jSecondaryArtistObj = jSecondaryArtistArr.getJSONObject(j);
                        secondaryArtist += jSecondaryArtistObj.getString("name");
                    }
                    SpotifyObject spotObject = new SpotifyObject(uri,"",id,type,images,name,secondaryArtist);
                    preferenceListItemArrayList.add(spotObject);
                    secondaryArtist = "";
                }else{
                    SpotifyObject spotObject = new SpotifyObject(uri,"",id,type,null,name,"");
                    preferenceListItemArrayList.add(spotObject);
                }
            }

           //Artists
            if (jArtists.length() > 0) {
                displayObject = new SpotifyObject("", "", "", "display", null, "Artists", "");
                preferenceListItemArrayList.add(displayObject);
            }

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
                preferenceListItemArrayList.add(spotObject);
                images = new ArrayList<>();
            }

            //Genres
            if (jGenres.length() > 0){
                displayObject = new SpotifyObject("","","","display",null,"Genres","");
                preferenceListItemArrayList.add(displayObject);
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
                preferenceListItemArrayList.add(spotObject);
            }


           //Tracks
            if (jTracks.length() > 0) {
                displayObject = new SpotifyObject("", "", "", "display", null, "Tracks", "");
                preferenceListItemArrayList.add(displayObject);
            }

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
                preferenceListItemArrayList.add(spotObject);
                secondaryArtist = "";
            }

           //Albums
            if (jAlbums.length() > 0) {
                displayObject = new SpotifyObject("", "", "", "display", null, "Albums", "");
                preferenceListItemArrayList.add(displayObject);
            }

            for (int i = 0; i < jAlbums.length(); i++) {
                JSONObject pref = jAlbums.getJSONObject(i);
                String type = pref.getString("type");
                id = pref.getString("spotify_id");
                name = pref.getString("name");
                JSONArray tempArr = pref.getJSONArray("images");
                if (tempArr.length() > 0) {
                    for (int j = 0; j < 3; j++) {
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
                preferenceListItemArrayList.add(spotObject);
                images = new ArrayList<>();
                secondaryArtist = "";
            }

        }catch(JSONException e){
            e.printStackTrace();
        }

        preferenceListAdapter = new PreferenceListAdapter(this,R.layout.activity_create_blacklist_preference, preferenceListItemArrayList);
        prefList.setAdapter(preferenceListAdapter);
        prefList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //PostNewPreference newPref = new PostNewPreference(position);
                //newPref.execute();
                SpotifyObject selectedPreference = preferenceListItemArrayList.get(position);
                try {
                    JSONObject body = new JSONObject();
                    body.put("spotify_uri", selectedPreference.getSpotifyURI());
                    body.put("name",selectedPreference.getSpotifyName());
                    PutPreference(body);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.createBlacklistPreferenceProgressBar).setVisibility(View.INVISIBLE);
    }
    public void PutPreference(JSONObject body) {
        JSONArray wrapper = new JSONArray();
        wrapper.put(body);
        apiHandler.putBlacklistPreferences(wrapper, (JSONObject) -> {
                try {
                    Toast.makeText(getBaseContext(), "Added: " + body.getString("name"), Toast.LENGTH_LONG).show();
                }catch(JSONException e){
                    e.printStackTrace();
                }

            });
    }
}

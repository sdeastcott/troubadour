package com.troubadour.troubadour.Activities;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.R;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ShowMoreActivity extends AppCompatActivity {

    Context context;
    ListView prefList;
    PreferenceListAdapter preferenceListAdapter;
    private APIHandler apiHandler;
    private ArrayList<SpotifyObject> preferenceListItemArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_more);

        Intent intent = getIntent();
        String query = intent.getStringExtra(CreatePreferenceActivity.EXTRA_QUERY);
        String preference = intent.getStringExtra(CreatePreferenceActivity.EXTRA_PREFERENCE);
        String title = preference.substring(0, 1).toUpperCase() + preference.substring(1) + 's';

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        search(query, preference);
    }

    // If Menu item "Back" is selected returns to previous Activity
    // Standard Super methods
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void search(String query, String preference) {
        apiHandler = new APIHandler(getApplicationContext());
        apiHandler.getSearch(query, preference, this::updateListView);
    }

    public void updateListView(JSONObject jsonObject) {
        preferenceListItemArrayList = new ArrayList<>();
        prefList = (ListView) findViewById(R.id.ShowMoreListView);

        try {
            String id;
            String name;
            String uri;
            String secondaryArtist = "";

            ArrayList<String> images = new ArrayList<>();
            JSONObject jData = jsonObject.getJSONObject("data");
            JSONArray jArtists = jData.optJSONArray("artists");
            JSONArray jTracks = jData.optJSONArray("tracks");
            JSONArray jAlbums = jData.optJSONArray("albums");
            JSONArray jSecondaryArtistArr;
            JSONObject jSecondaryArtistObj;

            // Artists
            if (jArtists != null) {
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
            }

            // Tracks
            if (jTracks != null) {
                for (int i = 0; i < jTracks.length(); i++) {
                    JSONObject pref = jTracks.getJSONObject(i);
                    String type = pref.getString("type");
                    id = pref.getString("spotify_id");
                    name = pref.getString("name");
                    uri = pref.getString("uri");
                    jSecondaryArtistArr = pref.getJSONArray("artists");

                    for (int j = 0; j < jSecondaryArtistArr.length(); j++) {
                        if (j != 0) {
                            secondaryArtist += ", ";
                        }
                        jSecondaryArtistObj = jSecondaryArtistArr.getJSONObject(j);
                        secondaryArtist += jSecondaryArtistObj.getString("name");
                    }

                    SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name, secondaryArtist);
                    preferenceListItemArrayList.add(spotObject);
                    secondaryArtist = "";
                }
            }


            // Albums
            if (jAlbums != null) {
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
                    for (int j = 0; j < jSecondaryArtistArr.length(); j++) {
                        if (j != 0) {
                            secondaryArtist += ", ";
                        }
                        jSecondaryArtistObj = jSecondaryArtistArr.getJSONObject(j);
                        secondaryArtist += jSecondaryArtistObj.getString("name");
                    }

                    SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name, secondaryArtist);
                    preferenceListItemArrayList.add(spotObject);
                    images = new ArrayList<>();
                    secondaryArtist = "";
                }
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }

        preferenceListAdapter = new PreferenceListAdapter(this, R.layout.activity_show_more, preferenceListItemArrayList);
        prefList.setAdapter(preferenceListAdapter);
        prefList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // PostNewPreference newPref = new PostNewPreference(position);
                // newPref.execute();
                SpotifyObject selectedPreference = preferenceListItemArrayList.get(position);

                try {
                    JSONObject body = new JSONObject();
                    body.put("spotify_uri", selectedPreference.getSpotifyURI());
                    body.put("name", selectedPreference.getSpotifyName());
                    PutPreference(body);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void PutPreference(JSONObject body) {
        JSONArray wrapper = new JSONArray();
        wrapper.put(body);
        apiHandler.putPreferences(wrapper, (JSONObject) -> {
            try {
                Toast.makeText(getBaseContext(), "Added: " + body.getString("name"), Toast.LENGTH_LONG).show();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
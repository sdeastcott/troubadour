package com.troubadour.troubadour.Activities;
import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;
import com.troubadour.troubadour.R;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PreferenceActivity extends AppCompatActivity {

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

        GetUserPreferences userPreferences = new GetUserPreferences(apiURL);
        userPreferences.execute();

        initUI();
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
            DeleteUserPreferences deleteUserPreferences = new DeleteUserPreferences(selectedPreferenceListItems, apiURL);
            deleteUserPreferences.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Populates ListView with a given jsonArray
    //Convert jsonObject to SpotifyObject then adds to a ListArray<SpotifyObject>
    //Sets Adapter to the ListArray<SpotifyObject>
    public void updateListView(JSONObject jsonObject){
        selectedPreferenceListItems = new ArrayList<>();
        lView = (ListView) findViewById(R.id.preferenceListView);

        try {
            SpotifyObject displayObject = null;
            String id = "";
            String name = "";
            String uri = "";
            String[] images = new String[3];

            //Log.e("data retrieval")
            JSONObject jData = jsonObject.getJSONObject("data");
            JSONArray jArtists = jData.getJSONArray("artists");
            JSONArray jTracks = jData.getJSONArray("tracks");
            JSONArray jAlbums = jData.getJSONArray("albums");

            if(jArtists.length() > 0) {
                displayObject = new SpotifyObject("", "", "", "display", null, "Artists");
                preferenceListItems.add(displayObject);
            }

            //Artists
            for (int i = 0; i < jArtists.length(); i++) {
                JSONObject pref = jArtists.getJSONObject(i);
                String type = pref.getString("type");
                id = pref.getString("spotify_id");
                name = pref.getString("name");
                JSONArray tempArr = pref.getJSONArray("images");
                if(tempArr.length() > 0) {
                    for (int j = 0; j < 3; j++) {
                        images[j] = tempArr.getJSONObject(j).getString("url");
                    }
                }
                uri = pref.getString("uri");

                SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name);
                preferenceListItems.add(spotObject);
                images = new String[3];
            }

            if(jTracks.length() > 0) {
                displayObject = new SpotifyObject("", "", "", "display", null, "Tracks");
                preferenceListItems.add(displayObject);
            }

            //Tracks
            for (int i = 0; i < jTracks.length(); i++) {
                JSONObject pref = jTracks.getJSONObject(i);
                String type = pref.getString("type");
                id = pref.getString("spotify_id");
                name = pref.getString("name");
                uri = pref.getString("uri");

                SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name);
                preferenceListItems.add(spotObject);
            }

            if(jAlbums.length() > 0) {
                displayObject = new SpotifyObject("", "", "", "display", null, "Albums");
                preferenceListItems.add(displayObject);
            }

            //Albums
            for (int i = 0; i < jAlbums.length(); i++) {
                JSONObject pref = jAlbums.getJSONObject(i);
                String type = pref.getString("type");
                id = pref.getString("spotify_id");
                name = pref.getString("name");
                JSONArray tempArr = pref.getJSONArray("images");
                if(tempArr.length() > 0) {
                    for (int j = 0; j < 3; j++) {
                        images[j] = tempArr.getJSONObject(j).getString("url");
                    }
                }
                uri = pref.getString("uri");

                SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name);
                preferenceListItems.add(spotObject);
                images = new String[3];
            }

        }catch(JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.content_preference, preferenceListItems);
        lView.setAdapter(lAdapter);
        lView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SpotifyObject selectedItem = preferenceListItems.get(position);
                if(selectedPreferenceListItems.contains(selectedItem.getSpotifyURI())){

                }else{
                    lView.setSelected(true);
                    selectedPreferenceListItems.add(selectedItem.getSpotifyURI());
                }

                //If the menu item for trash is not visible
                MenuItem item = prefMenu.findItem(R.id.trashCanPreferenceListActionBar);
                if(selectedPreferenceListItems.isEmpty() & item.isVisible()){
                    item.setVisible(false);
                }else if((!selectedPreferenceListItems.isEmpty()) & (!item.isVisible())){
                    item.setVisible(true);
                }
                return true;
            }
        });
    }

    public void initUI(){

        //readStaticJSON();

        lView = (ListView) findViewById(R.id.preferenceListView);
        PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.content_preference, preferenceListItems);
        lView.setAdapter(lAdapter);
        lView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SpotifyObject selectedItem = preferenceListItems.get(position);
                if(selectedPreferenceListItems.contains(selectedItem.getSpotifyURI())){

                }else{
                    lView.setSelected(true);
                    selectedPreferenceListItems.add(selectedItem.getSpotifyURI());
                }

                //If the menu item for trash is not visible
                MenuItem item = prefMenu.findItem(R.id.trashCanPreferenceListActionBar);
                if(selectedPreferenceListItems.isEmpty() & item.isVisible()){
                    item.setVisible(false);
                }else if((!selectedPreferenceListItems.isEmpty()) & (!item.isVisible())){
                    item.setVisible(true);
                }
                return true;
            }
        });
    }

    private class GetUserPreferences extends AsyncTask<Void, Void, Void> {

        private String android_id;
        private String apiurl;
        JSONObject jObject = null;

        private GetUserPreferences(String apiURL){
            preferenceListItems = new ArrayList<SpotifyObject>();
            apiurl = apiURL;
            android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }


        @Override
        protected Void doInBackground(Void... params){
            String result = "";
            String get_Data = "";
            try{
                Log.e("MY androidID: ", android_id);
                URL url = new URL(apiurl + "/preferences");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestProperty("X-USER-ID",android_id);
                httpURLConnection.setRequestProperty("Content-Type","application/json");
                Log.e("tag",Integer.toString(httpURLConnection.getResponseCode()));
                httpURLConnection.setRequestMethod("GET");

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while((line=bufferedReader.readLine()) != null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                jObject = encodeJSONArray(result);

            }catch(MalformedURLException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        //@SuppressWarnings({"UnusedDeclaration"})
        @Override
        protected void onPostExecute(Void Avoid){
            updateListView(jObject);
        }

        @Override
        protected void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }

        private JSONObject encodeJSONArray(String rawJson){
            JSONObject jsonObject= null;
            try {
                jsonObject = new JSONObject(rawJson);
            }
            catch(JSONException e){
                Log.e("JSON Parse","Result: " + rawJson + "|Error parsing data: " + e.toString());
            }

            return jsonObject;
        }

    }

    private class DeleteUserPreferences extends AsyncTask<Void, Void, Void> {

        private String android_id;
        private ArrayList<String> selectedPreferencesList;
        private String apiurl;
        private JSONObject jObject = null;

        private DeleteUserPreferences(ArrayList<String> selectedPreferences, String apiURL){
            apiurl = apiURL;
            selectedPreferencesList = selectedPreferences;
            android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }


        @Override
        protected Void doInBackground(Void... params){
            String result = "";
            String get_Data = "";
            try{
                Log.e("MY androidID: ", android_id);
                URL url = new URL(apiurl + "/preferences");
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("X-USER-ID",android_id);
                httpURLConnection.setRequestProperty("Content-Type","application/json");
                httpURLConnection.setRequestMethod("DELETE");


                JSONArray arr = new JSONArray();
                for(int i = 0; i < selectedPreferencesList.size(); i++){
                    arr.put(selectedPreferencesList.indexOf(i));
                }
                Log.e("DELETE DEBUG | ","spotify_uri's: "+selectedPreferencesList.toString());

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(arr.toString());

                httpURLConnection.connect();
                wr.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while((line=bufferedReader.readLine()) != null){
                    result+=line;
                }

                Log.e("tag",Integer.toString(httpURLConnection.getResponseCode()));
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                jObject = encodeJSONArray(result);

            }catch(MalformedURLException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        //@SuppressWarnings({"UnusedDeclaration"})
        @Override
        protected void onPostExecute(Void Avoid){
            GetUserPreferences userPreferences = new GetUserPreferences(apiURL);
            userPreferences.execute();
            updateListView(jObject);
        }

        @Override
        protected void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }

        private JSONObject encodeJSONArray(String rawJson){
            JSONObject jsonObject= null;
            try {
                jsonObject = new JSONObject(rawJson);
            }
            catch(JSONException e){
                Log.e("JSON Parse","Result: " + rawJson + "|Error parsing data: " + e.toString());
            }

            return jsonObject;
        }

    }
}

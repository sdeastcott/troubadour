package com.troubadour.troubadour.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.R;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/*CreatePreference Activity allows a user to enter new music preferences*/
public class CreatePreferenceActivity extends AppCompatActivity {

    String apiURL = "https://api.troubadour.tk";
    APIHandler api = new APIHandler();
    ListView prefList;
    PreferenceListAdapter preferenceListAdapter;
    ArrayList<SpotifyObject> preferenceListItemArrayList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Music Preferences");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        final EditText queryEdit = (EditText) findViewById(R.id.prefSearchEditText);
        Button createButton = (Button) findViewById(R.id.prefSearchButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dismisses Keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(queryEdit.getWindowToken(), 0);

                String query = queryEdit.getText().toString();
                QueryPreferences qPreferences = new QueryPreferences(query);
                qPreferences.execute();
            }
        });
    }

    //Populates ListView with a given jsonArray
    //Convert jsonObject to SpotifyObject then adds to a ListArray<SpotifyObject>
    //Sets Adapter to the ListArray<SpotifyObject>
    public void updateListView(JSONObject jsonObject){
        preferenceListItemArrayList = new ArrayList<>();
        prefList = (ListView) findViewById(R.id.prefSearchResultsListView);

       try {
            SpotifyObject displayObject = null;
            String id = "";
            String name = "";
            String uri = "";
            String[] images = new String[3];

            JSONObject jData = jsonObject.getJSONObject("data");
            JSONArray jArtists = jData.getJSONArray("artists");
            JSONArray jTracks = jData.getJSONArray("tracks");
            JSONArray jAlbums = jData.getJSONArray("albums");

            displayObject = new SpotifyObject("","","","display",null,"Artists");
            preferenceListItemArrayList.add(displayObject);

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
                preferenceListItemArrayList.add(spotObject);
                images = new String[3];
            }
            displayObject = new SpotifyObject("","","","display",null,"Tracks");
            preferenceListItemArrayList.add(displayObject);

            //Tracks
            for (int i = 0; i < jTracks.length(); i++) {
                JSONObject pref = jTracks.getJSONObject(i);
                String type = pref.getString("type");
                id = pref.getString("spotify_id");
                name = pref.getString("name");
                uri = pref.getString("uri");

                SpotifyObject spotObject = new SpotifyObject(uri, "", id, type, images, name);
                preferenceListItemArrayList.add(spotObject);
            }

            displayObject = new SpotifyObject("","","","display",null,"Albums");
            preferenceListItemArrayList.add(displayObject);


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
                preferenceListItemArrayList.add(spotObject);
                images = new String[3];
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        preferenceListAdapter = new PreferenceListAdapter(this,R.layout.activity_preference, preferenceListItemArrayList);
        prefList.setAdapter(preferenceListAdapter);
    }

    //Async Task class that performs the query 'inBackground' and updates the Preferences ListView 'onPostExecute'
    private class QueryPreferences extends AsyncTask<Void, Void, Void> {

        private String apiQuery;
        JSONObject jObject = null;

        private QueryPreferences(String url){
            apiQuery = url;
        }

        @Override
        protected Void doInBackground(Void... params){
            String result = "";
            String get_Data = "";
            try{
                URL url = new URL(apiURL + "/search?q=" + apiQuery);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
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

}

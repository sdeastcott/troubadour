package com.troubadour.troubadour.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.R;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/*CreatePreference Activity allows a user to enter new music preferences*/
public class CreatePreferenceActivity extends AppCompatActivity {

    Context context;
    ListView prefList;
    PreferenceListAdapter preferenceListAdapter;
    private APIHandler apiHandler;
    private ArrayList<SpotifyObject> preferenceListItemArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Music Preferences");
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
        apiHandler = new APIHandler(getApplicationContext());
        //apiHandler.getPreferences(this::updateListView);

        final EditText queryEdit = (EditText) findViewById(R.id.prefSearchEditText);
        Button createButton = (Button) findViewById(R.id.prefSearchButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
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
            String[] images = new String[3];

            JSONObject jData = jsonObject.getJSONObject("data");
            JSONArray jArtists = jData.getJSONArray("artists");
            JSONArray jTracks = jData.getJSONArray("tracks");
            JSONArray jAlbums = jData.getJSONArray("albums");

            if(jArtists.length() > 0) {
                displayObject = new SpotifyObject("", "", "", "display", null, "Artists");
                preferenceListItemArrayList.add(displayObject);
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
                preferenceListItemArrayList.add(spotObject);
                images = new String[3];
            }

            if(jTracks.length() > 0) {
                displayObject = new SpotifyObject("", "", "", "display", null, "Tracks");
                preferenceListItemArrayList.add(displayObject);
            }

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

            if(jAlbums.length() > 0) {
                displayObject = new SpotifyObject("", "", "", "display", null, "Albums");
                preferenceListItemArrayList.add(displayObject);
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
                preferenceListItemArrayList.add(spotObject);
                images = new String[3];
            }

        }catch(JSONException e){
            e.printStackTrace();
        }

        preferenceListAdapter = new PreferenceListAdapter(this,R.layout.activity_preference, preferenceListItemArrayList);
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
    }
    public void PutPreference(JSONObject body) {
        JSONArray wrapper = new JSONArray();
        wrapper.put(body);
        apiHandler.putPreferences(wrapper, (JSONObject) -> {
                try {
                    Toast.makeText(getBaseContext(), "Added: " + body.getString("name"), Toast.LENGTH_LONG).show();
                }catch(JSONException e){
                    e.printStackTrace();
                }

            });
    }

    /*
    public void GetPreferences(){
        //apiHandler.get
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
    */

    /*
    //Async Task class that performs the query 'inBackground' and updates the Preferences ListView 'onPostExecute'
    private class PostNewPreference extends AsyncTask<Void, Void, Void> {

        private int selectedPref;
        private String android_id;
        private SpotifyObject selectedSpotifyObject;
        private JSONObject jObject = null;

        private PostNewPreference(int position){
            selectedPref = position;
            android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        @Override
        protected Void doInBackground(Void... params){
            String result = "";
            String get_Data = "";
            try{
                selectedSpotifyObject = preferenceListItemArrayList.get(selectedPref);
                URL url = new URL(apiURL + "/preferences");

                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestProperty("X-USER-ID",android_id);
                httpURLConnection.setRequestProperty("Content-Type","application/json");

                JSONArray arr = new JSONArray();
                JSONObject body = new JSONObject();
                body.put("spotify_uri",selectedSpotifyObject.getSpotifyURI());
                body.put("name",selectedSpotifyObject.getSpotifyName());
                Log.e("PUT DEBUG | ","spotify_uri: "+selectedSpotifyObject.getSpotifyURI() + "spotify_name: " + selectedSpotifyObject.getSpotifyName());
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("PUT");
                arr.put(body);

                OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                wr.write(arr.toString());
                wr.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while((line=bufferedReader.readLine()) != null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                Log.e("response:", result);

                //jObject = encodeJSONArray(result);

            }catch(MalformedURLException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }catch(JSONException e){
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
            //updateListView(jObject);
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
    */
}

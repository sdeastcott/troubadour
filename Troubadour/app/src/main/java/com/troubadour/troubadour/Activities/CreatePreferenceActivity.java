package com.troubadour.troubadour.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.troubadour.troubadour.APIHandler;
import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.PreferenceListItem;
import com.troubadour.troubadour.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CreatePreferenceActivity extends AppCompatActivity {

    /* Stub for creating a new preference
    Need to populate after API and SDK added from Spotify
     */

    String apiURL = "http://api.troubadour.tk";//search?q=";
    APIHandler api = new APIHandler();
    ListView prefList;
    PreferenceListAdapter preferenceListAdapter;
    ArrayList<PreferenceListItem> preferenceListItemArrayList = new ArrayList();

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

    public void initUI(){
        final EditText queryEdit = (EditText) findViewById(R.id.prefSearchEditText);
        Button createButton = (Button) findViewById(R.id.prefSearchButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = queryEdit.getText().toString();
                QueryPreferences qPreferences = new QueryPreferences(query);
                qPreferences.execute();
            }
        });
    }

    public void updateListView(JSONArray jsonArray){
        prefList = (ListView) findViewById(R.id.preferenceListView);

        try {
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject pref = jsonArray.getJSONObject(i);
                String id = pref.getString("spotifyID");
                String type = pref.getString("type");
                String name = pref.getString("name");
                String uri = pref.getString("uri");

                PreferenceListItem prefItem = new PreferenceListItem(name, uri);
                preferenceListItemArrayList.add(prefItem);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        preferenceListAdapter = new PreferenceListAdapter(this,R.layout.activity_preference, preferenceListItemArrayList);
        prefList.setAdapter(preferenceListAdapter);
    }


    private class QueryPreferences extends AsyncTask<Void, Void, Void> {
        //View lView;
        private String apiQuery;
        JSONArray jArray = null;

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
                //httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                //get_Data = URLEncoder.encode("/search?q=","UTF-8") + URLEncoder.encode(apiQuery,"UTF-8");
                //bufferedWriter.write(get_Data);

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line;
                while((line=bufferedReader.readLine()) != null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                jArray = encodeJSONArray(result);

            }catch(MalformedURLException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        /*
        @Override
        protected Void doInBackground(Void... params){
            RESTClient

            String result = "";
            String get_Data = "";
            try{
                URL url = new URL(apiURL + "/search?q=" + apiQuery);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                Log.e("tag",Integer.toString(httpURLConnection.getResponseCode()));
                //httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                //get_Data = URLEncoder.encode("/search?q=","UTF-8") + URLEncoder.encode(apiQuery,"UTF-8");
                //bufferedWriter.write(get_Data);

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line;
                while((line=bufferedReader.readLine()) != null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                jArray = encodeJSONArray(result);

            }catch(MalformedURLException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }*/

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        //@SuppressWarnings({"UnusedDeclaration"})
        @Override
        protected void onPostExecute(Void Avoid){
            updateListView(jArray);
        }

        @Override
        protected void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }

        private JSONArray encodeJSONArray(String unencodedJSON){
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(unencodedJSON);
            }
            catch(JSONException e){
                Log.e("JSON Parse","Result: " + unencodedJSON + "|Error parsing data: " + e.toString());
            }

            return jsonArray;
        }

    }

}

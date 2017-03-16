package com.troubadour.troubadour.Activities;
import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;
import com.troubadour.troubadour.R;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.ArrayList;

public class PreferenceActivity extends AppCompatActivity {

    private Menu prefMenu;
    private MenuInflater prefMenuInflater;
    private ListView lView;
    private ArrayList<SpotifyObject> preferenceListItems = new ArrayList<>();
    private ArrayList<SpotifyObject> selectedPreferenceListItems = new ArrayList<>();

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
            //Delete list of selected preferences
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Initializes the UI Widgets in the Activity
    public void initUI(){

        readStaticJSON();

        lView = (ListView) findViewById(R.id.preferenceListView);
        PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.content_preference, preferenceListItems);
        lView.setAdapter(lAdapter);
        lView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SpotifyObject selectedItem = preferenceListItems.get(position);
                if(selectedPreferenceListItems.contains(selectedItem)){

                }else{
                    lView.setSelected(true);
                    selectedPreferenceListItems.add(selectedItem);
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

    //Statically reads a local JSON file, creates SpotifyObjects and adds them to the PreferencesListView
    public void readStaticJSON(){

        // Reading json file from assets folder
        BufferedReader br = null;
        InputStream is;
        AssetManager as;
        String temp;
        String input = "";
        try {

            //retrieves file from the assets folder
            as = getBaseContext().getAssets();
            is = as.open("samplePreferences.json");
            br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            while ((temp = br.readLine()) != null)
                input += temp;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ((br != null)) {
                try {
                    br.close(); // stop reading
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try{

            JSONObject jsonResponse = new JSONObject(input);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("preferences");

            //Add each JSON element to PrefListItem ArrayList
            for(int i = 0; i<jsonMainNode.length();i++){
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String name = jsonChildNode.optString("prefName");
                String seed = jsonChildNode.optString("prefSeed");
                SpotifyObject sObject = new SpotifyObject("","",seed,"track",null,name);
                preferenceListItems.add(sObject);
            }

        }
        catch(JSONException e){
            Toast.makeText(this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}

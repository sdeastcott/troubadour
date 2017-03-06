package com.troubadour.troubadour;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.troubadour.troubadour.Adapters.PreferenceListAdapter;
import com.troubadour.troubadour.TroubadourUser.PreferenceListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PreferenceActivity extends AppCompatActivity {

    ListView lView;
    ArrayList<PreferenceListItem> preferenceListItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeView();
    }

    public void initializeView(){

        // Reading json file from assets folder
        //StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        //FileReader fr = null;
        InputStream is = null;
        AssetManager as = null;
        String temp = "";
        String input = "";
        try {
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

            for(int i = 0; i<jsonMainNode.length();i++){
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String name = jsonChildNode.optString("prefName");
                String seed = jsonChildNode.optString("prefSeed");
                PreferenceListItem tempPref = new PreferenceListItem(name, seed);
                preferenceListItems.add(tempPref);
            }

        }
        catch(JSONException e){
            Toast.makeText(this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
        }

        lView = (ListView) findViewById(R.id.preferenceListView);
        PreferenceListAdapter lAdapter = new PreferenceListAdapter(lView.getContext(), R.layout.content_preference, preferenceListItems);
        lView.setAdapter(lAdapter);
    }

}

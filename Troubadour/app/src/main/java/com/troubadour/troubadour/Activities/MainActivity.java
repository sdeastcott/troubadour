package com.troubadour.troubadour.Activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.troubadour.troubadour.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }


    public void initUI(){
        Button preferenceListButton = (Button) findViewById(R.id.enterPreferencesButton);
        preferenceListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Changes activity to 'PreferenceActivity'
                Intent preferenceIntent = new Intent(MainActivity.this, PreferenceActivity.class);
                preferenceIntent.setClassName("com.troubadour.troubadour","com.troubadour.troubadour.Activities.PreferenceActivity");
                MainActivity.this.startActivity(preferenceIntent);
            }
         });

    }

    @SuppressWarnings("unused")
    public void displaySecret(){

        // Reading json file from assets folder
        BufferedReader br = null;
        InputStream is;
        AssetManager as;
        String temp;
        String input = "";
        try {

            //retrieves file from the assets folder
            as = getBaseContext().getAssets();
            is = as.open("troubadourAPI.secret");
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
            JSONArray jsonMainNode = jsonResponse.optJSONArray("spotifyAppCred");

            //Display first Secret
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(0);
                String cID = jsonChildNode.optString("ClientID");
                String cSecret = jsonChildNode.optString("ClientSecret");
            //Toast.makeText(this, "ClientID: '" +cID+ "' | ClientSecret: '" + cSecret, Toast.LENGTH_LONG).show();
            //String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            //Toast.makeText(this, "Android ID: " + android_id, Toast.LENGTH_LONG);
        }
        catch(JSONException e){
            Toast.makeText(this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}

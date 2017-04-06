package com.troubadour.troubadour.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE = 1337;
    private String CLIENT_ID;
    private String REDIRECT_URI = "troubadour://callback";
    private String troubadourSecret =  "troubadourAPI.secret";
    private String Token;
    private Button preferenceListButton;
    private Button logoutButton;
    private Button loginButton;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        CLIENT_ID = getCLIENT_ID();
    }

    public void initUI(){
        //Enter Preferences Button and Listener
        preferenceListButton = (Button) findViewById(R.id.enterPreferencesButton);
        preferenceListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Changes activity to 'PreferenceActivity'
                    Intent preferenceIntent = new Intent(MainActivity.this, PreferenceActivity.class);
                    preferenceIntent.setClassName("com.troubadour.troubadour", "com.troubadour.troubadour.Activities.PreferenceActivity");
                    MainActivity.this.startActivity(preferenceIntent);
                }
        });

        //Logout Button and Listender
        logoutButton = (Button) findViewById(R.id.logoutButton);
        sharedPref = this.getSharedPreferences("AuthenticationResponse", Context.MODE_PRIVATE);
        Token = sharedPref.getString("Token", null);
        if(Token == null) { logoutButton.setVisibility(View.GONE);}
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUser();
            }
        });

        //Login Button and Listener
        loginButton = (Button) findViewById(R.id.loginButton);
        sharedPref = this.getSharedPreferences("AuthenticationResponse", Context.MODE_PRIVATE);
        Token = sharedPref.getString("Token", null);
        if(Token != null) { loginButton.setVisibility(View.GONE);}
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void switchUser() {
        int REQUEST_CODE = 1337;
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(getCLIENT_ID(),
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setShowDialog(true);
        builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-modify-public"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    private void login() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-modify-public"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    //to catch the callback from the Spotify login activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                case TOKEN:
                    Context context = getApplicationContext();
                    SharedPreferences sharedPref = context.getSharedPreferences(
                            "AuthenticationResponse", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("Token", response.getAccessToken());
                    editor.commit();
                    Button loginButton = (Button) findViewById(R.id.loginButton);
                    Button logoutButton = (Button) findViewById(R.id.logoutButton);
                    loginButton.setVisibility(View.GONE);
                    logoutButton.setVisibility(View.VISIBLE);

                    break;

                case ERROR:
                    System.out.println(response.toString());
                    break;


                default:
                    //do nothing
            }
        }
    }

    //Retrieves the ClientID from Spotify
    public String getCLIENT_ID(){

        // Reading json file from assets folder
        BufferedReader br = null;
        InputStream is;
        AssetManager as;
        String temp;
        String input = "";

        try {

            //retrieves file from the assets folder
            as = getBaseContext().getAssets();
            is = as.open(troubadourSecret);
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
            JSONObject jsonMainNode = jsonResponse.getJSONObject("spotifyAppCred");

            String strClient = jsonMainNode.getString("ClientID");
            return strClient;
        }
        catch(JSONException e){
            Toast.makeText(this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}

package com.troubadour.troubadour.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import com.troubadour.troubadour.R;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginActivity extends Activity
{

    private String CLIENT_ID;
    private String REDIRECT_URI = "troubadour://callback";
    private int REQUEST_CODE = 1337;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        CLIENT_ID = getCLIENT_ID();
        openLogin();
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
                    System.out.println("Valid TOKEN");
                    sendAuthentication(response);
                    break;

                case ERROR:
                    System.out.println("SpotifyAPI Error: " + response.toString());
                    break;


                default:
                    //do nothing
            }
        }
    }


    private void openLogin() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-modify-public"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    private void sendAuthentication(AuthenticationResponse response) {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                "AuthenticationResponse", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Token", response.getAccessToken());
        editor.commit();
    }


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

            JSONObject jsonResponseObj = new JSONObject(input);
            JSONObject jsonMainNode = jsonResponseObj.getJSONObject("spotifyAppCred");

            String strClient = jsonMainNode.getString("ClientID");
            return strClient;
        }
        catch(JSONException e){
            Toast.makeText(this, "Error"+e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}
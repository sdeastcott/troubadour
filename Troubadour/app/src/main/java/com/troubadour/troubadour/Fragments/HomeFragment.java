package com.troubadour.troubadour.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.troubadour.troubadour.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private int REQUEST_CODE = 1337;
    private String CLIENT_ID;
    private String REDIRECT_URI = "troubadour://callback";
    private String troubadourSecret =  "troubadourAPI.secret";
    private String Token;
    private Button logoutButton;
    private Button loginButton;
    private SharedPreferences sharedPref;
    private View fragView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        CLIENT_ID = getCLIENT_ID();
        fragView = inflater.inflate(R.layout.fragment_home, container, false);
        initUI();
        return fragView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        menuInflater.inflate(R.menu.troubadour_menu, menu);
        menu.findItem(R.id.trashCanPreferenceListActionBar).setVisible(false);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initUI(){

        //Logout Button and Listender
        logoutButton = (Button) fragView.findViewById(R.id.logoutButton);
        sharedPref = getActivity().getSharedPreferences("AuthenticationResponse", Context.MODE_PRIVATE);
        Token = sharedPref.getString("Token", null);
        if(Token == null) { logoutButton.setVisibility(View.GONE);}
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchUser();
            }
        });

        //Login Button and Listener
        loginButton = (Button) fragView.findViewById(R.id.loginButton);
        sharedPref = getActivity().getSharedPreferences("AuthenticationResponse", Context.MODE_PRIVATE);
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
        AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
    }

    private void login() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming", "playlist-modify-public"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
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
            as = getActivity().getBaseContext().getAssets();
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
            Toast.makeText(getActivity(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}

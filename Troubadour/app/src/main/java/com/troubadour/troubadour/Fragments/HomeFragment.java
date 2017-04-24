package com.troubadour.troubadour.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.TimeUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.CustomClasses.TroubadourLocationManager;
import com.troubadour.troubadour.CustomClasses.TroubadourLocationObject;
import com.troubadour.troubadour.CustomClasses.LocationService;
import com.troubadour.troubadour.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private int REQUEST_CODE = 1337;
    private Date touchTime;
    private APIHandler apiHandler;
    private String CLIENT_ID;
    private String REDIRECT_URI = "troubadour://callback";
    private String troubadourSecret =  "troubadourAPI.secret";
    private String Token;
    private Button logoutButton;
    private Button loginButton;
    private Button generatePlaylistButton;
    private SharedPreferences sharedPref;
    private TroubadourLocationManager troubadourLocationManager;
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
        troubadourLocationManager = new TroubadourLocationManager(getContext());
        apiHandler = new APIHandler(getActivity(),getContext());
        initUI();
        return fragView;
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

        //Generate Playlist Button and Listener
        generatePlaylistButton = (Button) fragView.findViewById(R.id.generatePlaylistButton);
        sharedPref = getActivity().getSharedPreferences("AuthenticationResponse", Context.MODE_PRIVATE);
        Token = sharedPref.getString("Token", null);
        Log.e("Spotify APIToken","Token is: " + Token);
        if(Token == null){
            generatePlaylistButton.setAlpha(.5f);

        }else {
            generatePlaylistButton.setAlpha(1f);
        }
        /*
        generatePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        generatePlaylistButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    generatePlaylistButton.setBackgroundResource(R.drawable.troubadour_logo_text_dark_stroke_highlight);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP) {
                    Date newTime = new Date();
                    generatePlaylistButton.setBackgroundResource(R.drawable.troubadour_logo_text_dark_stroke);
                    Token = sharedPref.getString("Token", null);
                    if(Token == null) {
                        Toast.makeText(getActivity(), "Please Login to Spotify Premium in order to Generate a Playlist", Toast.LENGTH_SHORT).show();
                    }else if (touchTime == null){
                        DateFormat.getDateTimeInstance().format(new Date());
                        generatePlaylist();
                    }else if ( ((touchTime.getTime()) - (newTime.getTime()) / 1000 % 60) < 5){
                        generatePlaylist();
                        touchTime = newTime;
                    }
                }
                return true;
            }
        });
    }

    public void generatePlaylist(){
        TroubadourLocationObject locationObject = troubadourLocationManager.getLocation();
        Double lat = locationObject.getLatitude();
        Double lon = locationObject.getLongitude();
        //String sLat = lat.toString();
        //String sLon  = lon.toString();
        String sLat = String.valueOf(33.2005847);
        String sLon = String.valueOf(-87.5228543);
        SharedPreferences sharedPref = getContext().getSharedPreferences(
                "Settings", Context.MODE_PRIVATE);
        String sRadius = sharedPref.getString("Radius", "30");

        sharedPref = getActivity().getSharedPreferences("AuthenticationResponse", Context.MODE_PRIVATE);
        Token = sharedPref.getString("Token", null);
        SharedPreferences nearbyPreferences = getActivity().getSharedPreferences("NearbyPreferences", Context.MODE_PRIVATE);
        String prefs = nearbyPreferences.getString("NearbyPreferences", null);
        String prefsArr[] = prefs.split(",");
        apiHandler.postPlaylist(sLat, sLon, sRadius, prefsArr, Token, this::openInNewWindow);
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
            as = getActivity().getApplicationContext().getAssets();
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
            JSONArray jsonMainNode = jsonResponse.optJSONArray("spotifyAppCred");

            JSONObject strClient = jsonMainNode.getJSONObject(0);
            return strClient.optString("ClientID");
        }
        catch(JSONException e){
            Toast.makeText(getActivity(), "Error"+e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void openInNewWindow(JSONObject jObjectResult){
        //do something

        try {
            SharedPreferences nearbyPreferences = getActivity().getSharedPreferences("NearbyPreferences",Context.MODE_PRIVATE);
            String userID = nearbyPreferences.getString("spotifyUserID",null);

            JSONObject jData = jObjectResult.getJSONObject("data");
            String resultURI = "spotify:playlist:" + jData.get("playlist_id").toString();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(resultURI));
            if(browserIntent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(browserIntent);
            else {
                Toast.makeText(getActivity(), "Your Playlist has been generated but you do not have Spotify installed", Toast.LENGTH_SHORT).show();
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

    }
}

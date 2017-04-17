package com.troubadour.troubadour.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.R;
import com.troubadour.troubadour.CustomClasses.TroubadourFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.jar.Manifest;

public class HomeActivity extends AppCompatActivity {

    private MenuInflater prefMenuInflater;
    private Menu prefMenu;
    private APIHandler apiHandler;
    private ArrayList<String> selectedPreferenceListItems;
    private static final int canUseLocation = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        requestLocationPermissions();

        //Sets up The ViewPager for the Fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TroubadourFragmentPagerAdapter(getSupportFragmentManager(),HomeActivity.this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        viewPager.setCurrentItem(1,false);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Troubadour");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        prefMenuInflater = getMenuInflater();
        prefMenu = menu;
        prefMenuInflater.inflate(R.menu.troubadour_menu, prefMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_settings){
           return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //to catch the callback from the Spotify login activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        int REQUEST_CODE = 1337;
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

    @Override
    public void onRequestPermissionsResult(int resultCode, String permissions[], int[] grantResults){
        switch(resultCode) {
            case canUseLocation:

                if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Troubadour Requires Location Permissions: Try Again", Toast.LENGTH_SHORT).show();
                    requestLocationPermissions();
                }
        }
    }

    public void requestLocationPermissions() {
        //If permission not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    canUseLocation
            );
        }
    }
}

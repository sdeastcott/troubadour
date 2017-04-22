package com.troubadour.troubadour.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
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
import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.troubadour.troubadour.CustomClasses.APIHandler;
import com.troubadour.troubadour.R;
import com.troubadour.troubadour.CustomClasses.TroubadourFragmentPagerAdapter;

public class HomeActivity extends AppCompatActivity {

    private MenuInflater prefMenuInflater;
    private Menu prefMenu;
    private static final int canUseLocation = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        //PermissionRequestor permRequestor= new PermissionRequestor();
        //permRequestor.execute();
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
        if(id == R.id.activity_preference_list_action_settings){
            Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        if(id == R.id.activity_preference_list_action_help){
            Intent helpIntent = new Intent(HomeActivity.this, HelpActivity.class);
            startActivity(helpIntent);
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
                    Button generatePlaylistButton = (Button) findViewById(R.id.generatePlaylistButton);
                    loginButton.setVisibility(View.GONE);
                    logoutButton.setVisibility(View.VISIBLE);
                    generatePlaylistButton.setAlpha(1f);

                    break;

                case ERROR:
                    System.out.println(response.toString());
                    break;


                default:
                    //do nothing
            }
        }
    }

    public void initUI(){
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

    public Menu getPrefMenu(){
        return prefMenu;
    }

    private class PermissionRequestor extends AsyncTask<Void,Void,Void> {

        public Void doInBackground(Void... params){
            requestLocationPermissions();
            return null;
        }

        public void onProgressUpdate(Void... progress){

        }

        public void onPostExecute(Void... result){
            initUI();
        }

        public void onRequestPermissionsResult(int resultCode, String permissions[], int[] grantResults) {
            switch (resultCode) {
                case canUseLocation:

                    if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(HomeActivity.this, "Troubadour Requires Location Permissions: Try Again", Toast.LENGTH_SHORT).show();
                        requestLocationPermissions();
                    }
            }
        }

        public void requestLocationPermissions() {
            //If permission not granted
            if (ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        HomeActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        canUseLocation
                );
            }
        }
    }


}

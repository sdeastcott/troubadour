package com.troubadour.troubadour.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.troubadour.troubadour.R;

public class SplashActvity extends AppCompatActivity {

    private static final int canUseLocation = 1;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_splash);
        thisActivity = this;
        if (ContextCompat.checkSelfPermission(thisActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(thisActivity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, canUseLocation);
        }else{
            startApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int resultCode, String permissions[], int[] grantResults) {
        switch (resultCode) {
            case canUseLocation:

                if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(thisActivity, "Troubadour Requires Location Permissions: Try Again", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(thisActivity, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, canUseLocation);
                }
                else{
                    startApp();
                }
        }
    }

    private void startApp(){
        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(SplashActvity.this, HomeActivity.class);
                    startActivity(intent);
                }
            }

        };
        timerThread.start();
    }

}

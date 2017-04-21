package com.troubadour.troubadour.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.troubadour.troubadour.R;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        createListeners();
    }

    private void createListeners() {
        CompoundButton updatePlaylistSwitch = (CompoundButton) findViewById(R.id.updatePlaylistSwitch);
        updatePlaylistSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Context context = getApplicationContext();
                SharedPreferences sharedPref = context.getSharedPreferences(
                        "Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if(isChecked) {
                    editor.putString("Update", "true");
                    editor.commit();
                }
                else {
                    editor.putString("Update", "false");
                    editor.commit();
                }
            }
        });

        SeekBar partyRadiusBar = (SeekBar)findViewById(R.id.partyRadius);
        partyRadiusBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {
                int partyRadius = 10 * (seekBar.getProgress() + 1); //some math to convert 0-9 to radius

                TextView radiusDisplay = (TextView)findViewById(R.id.radiusDisplay);
                radiusDisplay.setText(partyRadius + " meters");

                Context context = getApplicationContext();
                SharedPreferences sharedPref = context.getSharedPreferences(
                        "Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Radius", partyRadius + "");
                editor.commit();
            }
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
        });
    }

}

package com.troubadour.troubadour.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.troubadour.troubadour.R;

public class CreatePreferenceActivity extends AppCompatActivity {

    /* Stub for creating a new preference
    Need to populate after API and SDK added from Spotify
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_preference);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Music Preferences");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Adds the 'back' icon to the action bar
        initUI();
    }

    //If Menu item "Back" is selected returns to previous Activity
    //Standard Super methods
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initUI(){
        Button createButton = (Button) findViewById(R.id.createPreferenceButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPreference();
            }
        });
    }

    //Procedure for UI interaction (GetView 'Widget' -> Get String -> call constructor)
    public void createPreference(){
        EditText prefName = (EditText) findViewById(R.id.prefNameEditText);
        EditText prefSeed = (EditText) findViewById(R.id.prefSeedEditText);

        String pName = prefName.getText().toString();
        String pSeed = prefSeed.getText().toString();

        //API Call to new pref
    }

}

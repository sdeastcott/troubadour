package com.troubadour.troubadour;

import android.content.Intent;
import android.preference.*;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
    }


    public void initializeUI(){
        Button preferenceListButton = (Button) findViewById(R.id.enterPreferencesButton);
        preferenceListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.getContext()
                //MainActivity.this
                Intent preferenceIntent = new Intent(MainActivity.this, PreferenceActivity.class);
                preferenceIntent.setClassName("com.troubadour.troubadour","com.troubadour.troubadour.PreferenceActivity");
                MainActivity.this.startActivity(preferenceIntent);
            }
         });
    }

}

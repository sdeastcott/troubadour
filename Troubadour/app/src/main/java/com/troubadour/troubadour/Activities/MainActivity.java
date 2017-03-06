package com.troubadour.troubadour.Activities;

import android.content.Intent;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.troubadour.troubadour.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }


    public void initUI(){
        Button preferenceListButton = (Button) findViewById(R.id.enterPreferencesButton);
        preferenceListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Changes activity to 'PreferenceActivity'
                Intent preferenceIntent = new Intent(MainActivity.this, PreferenceActivity.class);
                preferenceIntent.setClassName("com.troubadour.troubadour","com.troubadour.troubadour.Activities.PreferenceActivity");
                MainActivity.this.startActivity(preferenceIntent);
            }
         });
    }

}

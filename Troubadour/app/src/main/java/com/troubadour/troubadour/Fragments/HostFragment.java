package com.troubadour.troubadour.Fragments;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.troubadour.troubadour.R;


public class HostFragment extends Fragment {

    private View fragView;
    private Menu prefMenu;
    private MenuInflater prefMenuInflater;

    public HostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        fragView = inflater.inflate(R.layout.fragment_host, container, false);

        RadioGroup radioGroup = (RadioGroup) fragView.findViewById(R.id.radioTabs);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.nearbyRadioButton) {
                    Toast.makeText(getActivity(), "Change to Nearby Preferences ListView", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.blacklistedRadioButton) {
                    Toast.makeText(getActivity(), "Change to Blacklisted ListView", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return fragView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        prefMenu = menu;
        prefMenuInflater = menuInflater;
        prefMenuInflater.inflate(R.menu.troubadour_menu, menu);
        prefMenu.findItem(R.id.trashCanPreferenceListActionBar).setVisible(false);
        super.onCreateOptionsMenu(prefMenu,prefMenuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

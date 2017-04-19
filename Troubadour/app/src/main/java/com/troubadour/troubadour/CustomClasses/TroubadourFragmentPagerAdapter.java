package com.troubadour.troubadour.CustomClasses;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.troubadour.troubadour.Fragments.HomeFragment;
import com.troubadour.troubadour.Fragments.HostFragment;
import com.troubadour.troubadour.Fragments.PreferencesFragment;

/**
 * Created by James on 4/14/2017.
 */

public class TroubadourFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Host Settings", "Home", "Enter Preferences" };

    public TroubadourFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new HostFragment();
            case 1:
                return new HomeFragment();
            case 2:
                return new PreferencesFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}

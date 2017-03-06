package com.troubadour.troubadour.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.troubadour.troubadour.R;
import com.troubadour.troubadour.TroubadourUser.PreferenceListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by James on 3/4/2017.
 */

public class PreferenceListAdapter extends ArrayAdapter<PreferenceListItem>{

        View customView;
        private Context lContext;
        private ArrayList<PreferenceListItem> lPreferenceListItem;
        private int lTextViewResourceId;
        private static LayoutInflater inflater = null;

        public PreferenceListAdapter(Context context, int textViewResourceId, ArrayList<PreferenceListItem> _lPreferenceListItem) {
            super(context, R.layout.custom_preference_list_row, _lPreferenceListItem);
            lPreferenceListItem = _lPreferenceListItem;
            lContext = context;
            lTextViewResourceId = textViewResourceId;
        }

        public int getCount() {
            return lPreferenceListItem.size();
        }

        public PreferenceListItem getItem(PreferenceListItem position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public static class ViewHolder {
            public TextView display_name;
            public TextView display_number;

        }

        public void udpateAdapter(ArrayList<PreferenceListItem> _lPreferenceListItem){
            lPreferenceListItem = _lPreferenceListItem;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater lInflater = LayoutInflater.from(getContext());
            customView = lInflater.inflate(R.layout.custom_preference_list_row,parent,false);

            PreferenceListItem preferenceListItem = getItem(position);
            TextView preferenceNameTextView = (TextView) customView.findViewById(R.id.preferenceNameText);
            TextView preferenceSeedTextView = (TextView) customView.findViewById(R.id.preferenceSeedText);

            preferenceNameTextView.setText(preferenceListItem.getPrefName());
            preferenceSeedTextView.setText(preferenceListItem.getPrefSeed());
            customView.setTag(preferenceListItem.getPrefSeed());

            return customView;

        }

        @Override
        public boolean areAllItemsEnabled(){
            return true;
        }

        public boolean isEnabled(int arg0){
            return true;
        }
}

package com.troubadour.troubadour.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.troubadour.troubadour.R;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by James on 3/4/2017.
 */
/*
Cant really explain much on this class, basically just defining a custom adapter from an ArrayList.
There are a lot of default methods that we don't use often but the key one is "getView" and sometimes updateAdapter

A custom adapter will allow you to configure a list row special ways.
 */

public class PreferenceListAdapter extends ArrayAdapter<SpotifyObject>{

        View customView;
        private Context lContext;
        private ArrayList<SpotifyObject> lPreferenceListItem;
        private int lTextViewResourceId;
        private static LayoutInflater inflater = null;

        public PreferenceListAdapter(Context context, int textViewResourceId, ArrayList<SpotifyObject> _lPreferenceListItem) {
            super(context, R.layout.custom_preference_list_row, _lPreferenceListItem);
            lPreferenceListItem = _lPreferenceListItem;
            lContext = context;
            lTextViewResourceId = textViewResourceId;
        }

        public int getCount() {
            return lPreferenceListItem.size();
        }

        public SpotifyObject getItem(int position) {
            return lPreferenceListItem.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        //Used to update the adapter with a new ArrayList<SpotifyObject>
        public void updateAdapter(ArrayList<SpotifyObject> _lPreferenceListItem){
            lPreferenceListItem = _lPreferenceListItem;
            notifyDataSetChanged();
        }

        //This describes what data is going where within our ListRow View
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            SpotifyObject sObject = lPreferenceListItem.get(position);
            String sObjectType = getItem(position).getSpotifyType();
            LayoutInflater lInflater = LayoutInflater.from(lContext);

            if(sObjectType.equals("album")){
                customView = lInflater.inflate(R.layout.custom_album_list_row,parent,false);
                TextView albumNameView = (TextView) customView.findViewById(R.id.albumListRowNameView);
                ImageView albumImageView = (ImageView) customView.findViewById(R.id.albumListRowImageView);

                //Set text and download the 64x64 res image to set image
                albumNameView.setText(sObject.getSpotifyName());
                DownloadImageTask albumImageTask = new DownloadImageTask(albumImageView,sObject.getSpotifyImages()[2]);
                albumImageTask.execute();
                customView.setTag(sObject.getSpotifyID());
            }else if(sObjectType.equals("track")){
                //customView = lInflater.inflate(R.layout.custom_track_list_row,parent,false);
                customView = lInflater.inflate(R.layout.custom_track_list_row,parent,false);
                TextView trackNameView = (TextView) customView.findViewById(R.id.trackListRowNameViewEdited);
                TextView trackArtistView = (TextView) customView.findViewById(R.id.trackListRowArtistViewEdited);

                trackNameView.setText(sObject.getSpotifyName());
                trackArtistView.setText(sObject.getSpotifyTrackArtist());
                customView.setTag(sObject.getSpotifyID());
            }else if(sObjectType.equals("artist")) {
                customView = lInflater.inflate(R.layout.custom_artist_list_row, parent, false);
                TextView artistNameView = (TextView) customView.findViewById(R.id.artistListRowNameView);
                ImageView artistImageView = (ImageView) customView.findViewById(R.id.artistListRowImageView);

                //Set text and download the 64x64 res image to set image
                artistNameView.setText(sObject.getSpotifyName());
                DownloadImageTask artistImageTask = new DownloadImageTask(artistImageView, sObject.getSpotifyImages()[2]);
                artistImageTask.execute();
                customView.setTag(sObject.getSpotifyID());
            }else if(sObjectType.equals("display")) {
                customView = lInflater.inflate(R.layout.custom_displayobjecttype_list_row, parent, false);
                TextView displayTypeNameView = (TextView) customView.findViewById(R.id.displayListRowNameView);

                displayTypeNameView.setText(sObject.getSpotifyName());
                customView.setTag(sObject.getSpotifyName());
            }else{
                customView = lInflater.inflate(R.layout.custom_preference_list_row,parent,false);
                customView.setTag(sObject.getSpotifyID());

                TextView preferenceNameTextView = (TextView) customView.findViewById(R.id.preferenceNameText);
                TextView preferenceSeedTextView = (TextView) customView.findViewById(R.id.preferenceSeedText);

                preferenceNameTextView.setText(sObject.getSpotifyName());
                preferenceSeedTextView.setText(sObject.getSpotifyID());

            }



            return customView;
        }

        @Override
        public boolean areAllItemsEnabled(){
            return true;
        }

        public boolean isEnabled(int arg0){
            return true;
        }

    //Async task class that is constructed with the ImageView to be rendered and the Source URL for the Image
    //Downloads the Bitmap for the image through the URL
    private class DownloadImageTask extends AsyncTask<Void, Void, Void> {

        private ImageView iView = null;
        private String imageURL = "";
        private Bitmap result = null;

        private DownloadImageTask(ImageView imageView, String url){
            this.iView = imageView;
            this.imageURL = url;
        }

        @Override
        protected Void doInBackground(Void... params){

            try{
                InputStream in = new java.net.URL(this.imageURL).openStream();
                result = BitmapFactory.decodeStream(in);
            }catch(MalformedURLException e) {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        //@SuppressWarnings({"UnusedDeclaration"})
        @Override
        protected void onPostExecute(Void Avoid){
            iView.setImageBitmap(result);
        }

        @Override
        protected void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }

    }
}

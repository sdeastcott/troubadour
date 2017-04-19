package com.troubadour.troubadour.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.troubadour.troubadour.CustomClasses.CircleImageView;
import com.troubadour.troubadour.R;
import com.troubadour.troubadour.CustomClasses.SpotifyObject;
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

        private View customView;
        private RequestQueue mRequestQueue;
        private ImageLoader mImageLoader;
        private Context lContext;
        private ArrayList<SpotifyObject> lPreferenceListItem;

        public PreferenceListAdapter(Context context, int textViewResourceId, ArrayList<SpotifyObject> _lPreferenceListItem) {
            super(context, R.layout.custom_preference_list_row, _lPreferenceListItem);
            lPreferenceListItem = _lPreferenceListItem;
            lContext = context;
            mRequestQueue = Volley.newRequestQueue(lContext);
            initImageLoader();
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

        public void initImageLoader(){
            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache(){
                private final LruCache<String,Bitmap> mCache = new LruCache<>(10);
                public void putBitmap(String url, Bitmap bitmap){
                    mCache.put(url,bitmap);
                }
                public Bitmap getBitmap(String url){
                    return mCache.get(url);
                }
            });
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
                TextView artistNameView = (TextView) customView.findViewById(R.id.albumListRowArtistView);
                CircleImageView albumImageView = (CircleImageView) customView.findViewById(R.id.albumListRowImageView);


                //Set text and download the 64x64 res image to set image
                albumNameView.setText(sObject.getSpotifyName());
                artistNameView.setText(sObject.getSpotifySecondaryArtist());

                if(sObject.getSpotifyImages() == null){
                    Bitmap bm = getBitmapFromVectorDrawable(lContext, R.mipmap.ic_launcher);
                    albumImageView.setLocalImageBitmap(bm);
                    albumImageView.setDefaultImageResId(R.mipmap.ic_blank);
                    albumImageView.setBackgroundColor(Color.argb(255,48,48,48));

                }else {
                    int lastImage = sObject.getSpotifyImages().size();
                    if (lastImage == 0) {
                        Bitmap bm = getBitmapFromVectorDrawable(lContext, R.mipmap.ic_launcher);
                        albumImageView.setLocalImageBitmap(bm);
                        albumImageView.setDefaultImageResId(R.mipmap.ic_blank);
                        albumImageView.setBackgroundColor(Color.argb(255,48,48,48));

                    } else {
                        String image = sObject.getSpotifyImages().get(lastImage - 1);
                        albumImageView.setImageUrl(image, mImageLoader);
                        albumImageView.setDefaultImageResId(R.mipmap.ic_blank);
                        albumImageView.setBackgroundColor(Color.argb(255,48,48,48));
                    }
                }
                customView.setTag(sObject.getSpotifyID());
            }else if(sObjectType.equals("track")){
                //customView = lInflater.inflate(R.layout.custom_track_list_row,parent,false);
                customView = lInflater.inflate(R.layout.custom_track_list_row,parent,false);
                TextView trackNameView = (TextView) customView.findViewById(R.id.trackListRowNameView);
                TextView trackArtistView = (TextView) customView.findViewById(R.id.trackListRowArtistView);

                trackNameView.setText(sObject.getSpotifyName());
                trackArtistView.setText(sObject.getSpotifySecondaryArtist());
                customView.setTag(sObject.getSpotifyID());
            }else if(sObjectType.equals("artist")) {
                customView = lInflater.inflate(R.layout.custom_artist_list_row, parent, false);
                TextView artistNameView = (TextView) customView.findViewById(R.id.artistListRowNameView);
                CircleImageView artistImageView = (CircleImageView) customView.findViewById(R.id.artistListRowImageView);

                //Set text and download the 64x64 res image to set image
                if(sObject.getSpotifyImages() == null) {
                        Bitmap bm = getBitmapFromVectorDrawable(lContext, R.mipmap.ic_launcher);
                        artistImageView.setDefaultImageResId(R.mipmap.ic_blank);
                        artistImageView.setLocalImageBitmap(bm);
                        artistImageView.setBackgroundColor(Color.BLACK);
                        artistImageView.setBackgroundColor(Color.argb(255,48,48,48));
                }else{
                    int lastImage = sObject.getSpotifyImages().size();
                    if (lastImage == 0) {
                        Bitmap bm = getBitmapFromVectorDrawable(lContext, R.mipmap.ic_launcher);
                        artistImageView.setLocalImageBitmap(bm);
                        artistImageView.setDefaultImageResId(R.mipmap.ic_blank);
                        artistImageView.setBackgroundColor(Color.argb(255,48,48,48));

                    } else {
                        String image = sObject.getSpotifyImages().get(lastImage - 1);
                        artistImageView.setImageUrl(image, mImageLoader);
                        artistImageView.setDefaultImageResId(R.mipmap.ic_blank);
                        artistImageView.setBackgroundColor(Color.argb(255,48,48,48));
                    }
                }
                artistNameView.setText(sObject.getSpotifyName());
                customView.setTag(sObject.getSpotifyID());
            }else if(sObjectType.equals("genre")){
                customView = lInflater.inflate(R.layout.custom_genre_list_row,parent,false);
                TextView genreNameView = (TextView) customView.findViewById(R.id.genreListRowNameView);
                //NetworkImageView genreImageView = (NetworkImageView) customView.findViewById(R.id.genreListRowImageView);

                //String image = sObject.getSpotifyImages()[2];
                //genreImageView.setImageUrl(image,mImageLoader);
                genreNameView.setText(sObject.getSpotifyName());
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

    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}

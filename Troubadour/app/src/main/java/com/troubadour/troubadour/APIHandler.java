package com.troubadour.troubadour;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.SpotifyApi;

/**
 * Created by BBKiel on 3/6/2017.
 */

public class APIHandler {

    SpotifyApi api;
    String apiURL = "https://api.troubadour.tk/";

    public APIHandler(){
        api = new SpotifyApi();
    }


    public JSONArray queryPreference(String query){
        String url = "search?q=" + query;
        GETCallHelper apiHelper = new GETCallHelper(url);
        try{
            return apiHelper.execute().get();
        }
        catch(InterruptedException e){
            e.printStackTrace();
            return null;
        }
        catch(ExecutionException e){
            e.printStackTrace();
            return null;
        }
    }

    private class GETCallHelper extends AsyncTask<Void, Void, JSONArray> {
        //View lView;
        private String apiQuery;
        JSONArray jArray = null;

        private GETCallHelper(String url){
            apiQuery = url;
        }

        @Override
        protected JSONArray doInBackground(Void... params){
            String result = "";
            try{
                URL url = new URL(apiQuery);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("Get");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                //get_Data = URLEncoder.encode("func", "UTF-8") + "=" + URLEncoder.encode("denyRestaurantSubmission","UTF-8") + "&" + URLEncoder.encode("itemID", "UTF-8") + "=" + URLEncoder.encode(itemID,"UTF-8");
                //bufferedWriter.write(get_Data);

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String line;
                while((line=bufferedReader.readLine()) != null){
                    result+=line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                jArray = encodeJSONArray(result);

            }catch(MalformedURLException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
                e.printStackTrace();
            }
            return jArray;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @SuppressWarnings({"UnusedDeclaration"})
        protected void onPostExecute(Void Avoid){
        }

        @Override
        protected void onProgressUpdate(Void... values){
            super.onProgressUpdate(values);
        }

        private JSONArray encodeJSONArray(String unencodedJSON){
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(unencodedJSON);
            }
            catch(JSONException e){
                Log.e("JSON Parse","Result: " + unencodedJSON + "|Error parsing data: " + e.toString());
            }

            return jsonArray;
        }

    }

}

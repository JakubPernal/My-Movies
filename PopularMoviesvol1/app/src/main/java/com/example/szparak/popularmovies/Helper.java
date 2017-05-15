package com.example.szparak.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Szparak on 27.04.2017.
 */

public class Helper {

    public static String handleUrlConnection(Uri uri){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = null;
        String line;

        try {
            URL givenUrl = new URL(uri.toString());
            urlConnection = (HttpURLConnection) givenUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }

            jsonString = buffer.toString();

        } catch (IOException e) {
            Log.e("Helper OpenConnection", "Error", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }

        return jsonString;

    }


}

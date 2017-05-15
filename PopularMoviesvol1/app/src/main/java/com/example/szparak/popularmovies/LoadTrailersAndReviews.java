package com.example.szparak.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.example.szparak.popularmovies.DetailActivity.DetailsFragment.review;
import static com.example.szparak.popularmovies.DetailActivity.DetailsFragment.reviewerName;
import static com.example.szparak.popularmovies.DetailActivity.DetailsFragment.videoKeys;
import static com.example.szparak.popularmovies.DetailActivity.DetailsFragment.videoNames;

/**
 * Created by Szparak on 26.04.2017.
 */

public class LoadTrailersAndReviews extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = LoadingImages.class.getSimpleName();
    final String RESULTS = "results";
    DetailActivity.DetailsFragment detailsFragment;
    AsyncTaskCompleteListener<List<String>> listener;

    LoadTrailersAndReviews(DetailActivity.DetailsFragment detailsFragment, AsyncTaskCompleteListener<List<String>> listener){
        this.detailsFragment = detailsFragment;
        this.listener = listener;
    }

    private void getMovieVideos(String videosTrailersJsonString) throws JSONException {

        final String KEY = "key";
        final String NAME = "name";

        JSONObject sortedJson = new JSONObject(videosTrailersJsonString);
        JSONArray movieArray = sortedJson.getJSONArray(RESULTS);


        for(int i=0; i<movieArray.length(); i++){

            JSONObject theMovie = movieArray.getJSONObject(i);
            videoNames.add(theMovie.getString(NAME));
            videoKeys.add(theMovie.getString(KEY));
        }
    }

    private void getMovieReviews(String videosTrailersJsonString) throws JSONException{

        final String CONTENT = "content";
        final String AUTHOR = "author";

        JSONObject sortedJson = new JSONObject(videosTrailersJsonString);
        JSONArray movieArray = sortedJson.getJSONArray(RESULTS);

        for(int i=0;i<movieArray.length();i++){

            JSONObject theMovie = movieArray.getJSONObject(i);
            reviewerName.add(theMovie.getString(AUTHOR));
            review.add(theMovie.getString(CONTENT));
        }

    }



    @Override
    protected Void doInBackground(String... strings) {

            final String BASE_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
            final String API_KEY = "api_key";


            Uri videosUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                    .appendPath(strings[0])
                    .appendPath(strings[1])
                    .appendQueryParameter(API_KEY, BuildConfig.MY_API_KEY)
                    .build();

            Uri reviewsUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                    .appendPath(strings[0])
                    .appendPath(strings[2])
                    .appendQueryParameter(API_KEY, BuildConfig.MY_API_KEY)
                    .build();


            try {
                getMovieVideos(Helper.handleUrlConnection(videosUri));
                getMovieReviews(Helper.handleUrlConnection(reviewsUri));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onTaskCompleted(null);
    }
}

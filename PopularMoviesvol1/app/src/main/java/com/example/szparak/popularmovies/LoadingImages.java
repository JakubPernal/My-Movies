package com.example.szparak.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.szparak.popularmovies.MainFragment.movieIDs;
import static com.example.szparak.popularmovies.MainFragment.movieOverview;
import static com.example.szparak.popularmovies.MainFragment.movieRDate;
import static com.example.szparak.popularmovies.MainFragment.movieTitles;
import static com.example.szparak.popularmovies.MainFragment.movieVAverage;

/**
 * Created by Szparak on 07.04.2017.
 */
public class LoadingImages extends AsyncTask<String, Void, String[]> {

    String[] posters;
    private MainFragment mainFragment;
    private final String LOG_TAG = LoadingImages.class.getSimpleName();
    private AsyncTaskCompleteListener<String[]> listener;

    public LoadingImages(MainFragment mainFragment, AsyncTaskCompleteListener<String[]> listener) {
        this.mainFragment = mainFragment;
        this.listener = listener;
    }

    private String[] getMovieData(String moviesJsonString, int posterNumbers) throws JSONException {

        final String RESULTS = "results";
        final String POSTER_PATH = "poster_path";
        final String TITLE = "title";
        final String RELEASE_DATE = "release_date";
        final String VOTE_AVERAGE = "vote_average";
        final String OVERVIEW = "overview";
        final String ID = "id";
        String[] posters = new String[posterNumbers];

        JSONObject sortedJson = new JSONObject(moviesJsonString);
        JSONArray movieArray = sortedJson.getJSONArray(RESULTS);

        movieTitles = new String[posterNumbers];
        movieRDate = new String[posterNumbers];
        movieVAverage = new String[posterNumbers];
        movieOverview = new String[posterNumbers];
        movieIDs = new String[posterNumbers];

        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject theMovie = movieArray.getJSONObject(i);
            posters[i] = theMovie.getString(POSTER_PATH);
            movieTitles[i] = theMovie.getString(TITLE);
            movieRDate[i] = theMovie.getString(RELEASE_DATE);
            movieVAverage[i] = theMovie.getString(VOTE_AVERAGE);
            movieOverview[i] = theMovie.getString(OVERVIEW);
            movieIDs[i] = theMovie.getString(ID);


        }

        for (String s : posters) {
            Log.v(LOG_TAG, "Forecast entry: " + s);
        }


        return posters;
    }

    private String[] makeFullPosterUrl(String[] posterUrl, int posterNumbers) {
        String[] fullUrls = new String[posterNumbers];

        final String BASE_URL = "https://image.tmdb.org/t/p";
        final String FILE_SIZE = "/w500";

        for (int i = 0; i < posterUrl.length; i++) {
            fullUrls[i] = BASE_URL + FILE_SIZE + posterUrl[i];
        }

        return fullUrls;
    }


    @Override
    protected String[] doInBackground(String... strings) {

        int posterNumbers = 20;
        posters = new String[posterNumbers];




            final String BASE_MOVIE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String COUNT_PARAM = "vote_count.gte";
            final String API_KEY = "api_key";


            Uri sortUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, strings[0])
                    .appendQueryParameter(COUNT_PARAM, strings[1])
                    .appendQueryParameter(API_KEY, BuildConfig.MY_API_KEY)
                    .build();


        try {
            posters = makeFullPosterUrl(getMovieData(Helper.handleUrlConnection(sortUri), posterNumbers), posterNumbers);
            return posters;
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
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);
        listener.onTaskCompleted(strings);


    }


}

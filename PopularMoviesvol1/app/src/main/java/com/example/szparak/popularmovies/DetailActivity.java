package com.example.szparak.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    Toolbar myToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        myToolbar = (Toolbar) findViewById(R.id.my_detail_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.details_label));
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_detail, new DetailsFragment())
                    .commit();
        }



    }



    public static class DetailsFragment extends Fragment implements AsyncTaskCompleteListener<List<String>>, LoaderManager.LoaderCallbacks<Cursor> {

        public static List<String> videoNames = new ArrayList<String>();
        public static List<String> videoKeys = new ArrayList<String>();
        public static List<String> reviewerName = new ArrayList<String>();
        public static List<String> review = new ArrayList<String>();

        private ImageView poster;
        private TextView title,release_date,vote_average,overview;
        private Button favourite_button;
        private String posterUrl;
        private String posterUrlTable[];
        private String title_string,release_date_string,vote_average_string,overview_string,movieId;
        private int flag;
        NestedListView reviewListView;
        NestedListView trailersListView;
        TrailersAdapter tAdapter;
        ReviewsAdapter rAdapter;
        Cursor cursor, mCursor;
        View emptyTrailersTextView, emptyReviewsTextView, emptyTrailers, emptyReviews;
        private static final String[] PROJECTION = new String[] {MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, MoviesContract.MoviesEntry.COLUMN_TITLE, MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
                MoviesContract.MoviesEntry.COLUMN_RATE, MoviesContract.MoviesEntry.COLUMN_OVERVIEW};

        private static final int LOADER_ID = 11;

        public DetailsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            poster = (ImageView) rootView.findViewById(R.id.poster);
            title = (TextView) rootView.findViewById(R.id.title);
            release_date = (TextView) rootView.findViewById(R.id.release_date);
            vote_average = (TextView) rootView.findViewById(R.id.vote_average);
            overview = (TextView) rootView.findViewById(R.id.overview);
            favourite_button = (Button) rootView.findViewById(R.id.favourite_button);
            favourite_button.setBackgroundColor(getResources().getColor(R.color.buttonUnpressedColor));
            emptyTrailers = (ProgressBar) rootView.findViewById(R.id.trailers_progressBar);
            emptyReviews = (ProgressBar) rootView.findViewById(R.id.reviews_progressBar);



            favourite_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri buildUri = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon()
                            .appendPath(movieId)
                            .build();

                    Cursor cursor = getContext().getContentResolver().query(buildUri,null,null,null,null);

                    if (cursor.getCount() == 0) {

                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movieId);
                        contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTER_URL, posterUrl);
                        contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, title_string);
                        contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, release_date_string);
                        contentValues.put(MoviesContract.MoviesEntry.COLUMN_RATE, vote_average_string);
                        contentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, overview_string);

                        getContext().getContentResolver().insert(buildUri, contentValues);
                        favourite_button.setText(getString(R.string.button_text2));
                        favourite_button.setBackgroundColor(getResources().getColor(R.color.buttonPressedColor));
                        Toast.makeText(getContext(), "Movie successfully added to favourites", Toast.LENGTH_SHORT).show();

                    } else {
                        if(getContext().getContentResolver().delete(buildUri,null,null)==1) {
                            Toast.makeText(getContext(), "Movie successfully removed from favourites", Toast.LENGTH_SHORT).show();
                            favourite_button.setText(getString(R.string.button_text));
                            favourite_button.setBackgroundColor(getResources().getColor(R.color.buttonUnpressedColor));
                            getLoaderManager().destroyLoader(LOADER_ID);
                        }
                    }
                }
            });

            Intent intent = getActivity().getIntent();
            Bundle myBundle = intent.getExtras();
            if(!myBundle.isEmpty()){
                posterUrl = myBundle.getString(MainFragment.POSTER_PATH);

                String[] argumentSelection = new String[]{posterUrl};

                cursor = getContext().getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,null,MoviesContract.MoviesEntry.COLUMN_POSTER_URL + " = ? ",argumentSelection, null);

                if(cursor.getCount() == 0){
                    title_string = myBundle.getString(MainFragment.TITLE);
                    release_date_string = myBundle.getString(MainFragment.RELEASE_DATE);
                    vote_average_string = myBundle.getString(MainFragment.VOTE_AVERAGE);
                    overview_string = myBundle.getString(MainFragment.OVERVIEW);
                    movieId = myBundle.getString(MainFragment.ID);
                }
            }

            Picasso.with(getActivity()).load(posterUrl).into(poster);

            if (cursor.getCount() == 0) {
                setViews();

                LoadTrailersAndReviews videoTask = new LoadTrailersAndReviews(this, this);
                videoTask.execute(movieId, "videos", "reviews");
            } else {
                favourite_button.setText(getString(R.string.button_text2));
                favourite_button.setBackgroundColor(getResources().getColor(R.color.buttonPressedColor));
                getLoaderManager().initLoader(LOADER_ID,null,this);
            }



            trailersListView = (NestedListView) rootView.findViewById(R.id.trailers);
            reviewListView = (NestedListView) rootView.findViewById(R.id.reviews);
            trailersListView.setEmptyView(emptyTrailers);
            reviewListView.setEmptyView(emptyReviews);

            trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    PackageManager packageManager = getActivity().getPackageManager();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoKeys.get(i)));
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent);
                    }
                }
            });



            return rootView;
        }



        @Override
        public void onDestroyView() {
            videoNames.clear();
            videoKeys.clear();
            reviewerName.clear();
            review.clear();
            super.onDestroyView();
        }

        @Override
        public void onTaskCompleted(List<String> results) {
            if (videoNames.size() == 0) {
                emptyTrailers.setVisibility(View.INVISIBLE);
            } else {
                tAdapter = new TrailersAdapter(getActivity(), videoNames.toArray(new String[videoNames.size()]));
                trailersListView.setAdapter(tAdapter);
            }
            if (review.size() == 0) {
                emptyReviews.setVisibility(View.INVISIBLE);
            } else {
                rAdapter = new ReviewsAdapter(getActivity(), reviewerName.toArray(new String[reviewerName.size()]), review.toArray(new String[review.size()]));
                reviewListView.setAdapter(rAdapter);
            }

            Log.i("ilosc recenzji rowna" , Integer.toString(review.size()));
        }


        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
            switch (loaderId) {

                case LOADER_ID:

                    Uri queryUri = MoviesContract.MoviesEntry.CONTENT_URI;
                    posterUrlTable = new String[]{posterUrl};

                    return new CursorLoader(getContext(),
                            queryUri,
                            PROJECTION,
                            MoviesContract.MoviesEntry.COLUMN_POSTER_URL + " = ? ",
                            posterUrlTable,
                            null);

                default:
                    throw new RuntimeException("Loader Not Implemented: " + loaderId);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            swapCursor(data);
            getAndLoadFromCursor();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            swapCursor(null);
        }

        public void getAndLoadFromCursor(){
            if (mCursor!=null) {
                mCursor.moveToPosition(0);
                movieId = mCursor.getString(0);
                title_string = mCursor.getString(1);
                release_date_string = mCursor.getString(2);
                vote_average_string = mCursor.getString(3);
                overview_string = mCursor.getString(4);

                setViews();

                LoadTrailersAndReviews videoTask = new LoadTrailersAndReviews(this,this);
                videoTask.execute(movieId, "videos" , "reviews");
            }
        }

        public void swapCursor(Cursor cursor){
            mCursor = cursor;
        }

        public void setViews(){
            title.setText(title_string);
            release_date.setText(release_date_string);
            vote_average.setText(vote_average_string +"/10");
            overview.setText(overview_string);
        }

    }

}

package com.example.szparak.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szparak on 05.03.2017.
 */
public class MainFragment extends Fragment implements AsyncTaskCompleteListener<String[]>,
        LoaderManager.LoaderCallbacks<Cursor>{


    private final String LOG_TAG = LoadingImages.class.getSimpleName();
    private ImageListAdapter adapter,adapter2;
    private static  GridView myGridView;
    public static String[] movieTitles;
    public static String[] movieRDate;
    public static String[] movieVAverage;
    public static String[] movieOverview;
    public static String[] movieIDs;
    public static final String TITLE = "title";
    public static final String RELEASE_DATE = "release_date";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String OVERVIEW = "overview";
    public static final String POSTER_PATH = "poster_path";
    public static final String ID = "id";
    public static ManageToolbar manageToolbar;
    private static int flag=0,menuFlag=0,movementFlag=0,pMenuFlag=2;
    private static int currentFirstVisibleItem;
    private static int index;
    private static final int IMAGES_LOADER_ID = 10;
    private static List<String> posterUrls = new ArrayList<>();

    private static final String[] IMAGES_PROJECTION = new String[] {MoviesContract.MoviesEntry.COLUMN_POSTER_URL};


    public MainFragment(){}

    public MainFragment(ManageToolbar manageToolbar) {
        this.manageToolbar = manageToolbar;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState==null) {
            LoadingImages defaultTask = new LoadingImages(this, this);
            defaultTask.execute("", "");
        }

//        setRetainInstance(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filtermenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.highest_rated) {
            LoadingImages highestRateTask = new LoadingImages(this, this);
            highestRateTask.execute("vote_average.desc", "500");
            ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.highest_rated_label));
            menuFlag=3;
            pMenuFlag=menuFlag;
            movementFlag=0;
            return true;
        }

        if(id == R.id.most_popular){
            LoadingImages mostPopularTask = new LoadingImages(this, this);
            mostPopularTask.execute("popularity.desc", "");
            ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.most_popular_label));
            menuFlag=2;
            pMenuFlag=menuFlag;
            movementFlag=0;
            return true;
        }
        if(id == R.id.favourites){
            menuFlag=1;
            getLoaderManager().initLoader(IMAGES_LOADER_ID,null,this);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        switch (menuFlag) {
            case 1:
                getLoaderManager().initLoader(IMAGES_LOADER_ID,null,this);
            case 2:
                ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.most_popular_label));
                break;
            case 3:
                ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.highest_rated_label));
                break;
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        myGridView = (GridView) rootView.findViewById(R.id.usage_example_gridview);



        adapter = new ImageListAdapter(getActivity(), posterUrls.toArray(new String[posterUrls.size()]));
        myGridView.setAdapter(adapter);
        View emptyView = rootView.findViewById(R.id.posters_empty_textView);
        myGridView.setEmptyView(emptyView);







        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                Bundle myBundle = new Bundle();

                String[] argumentSelection = new String[]{posterUrls.get(i)};

                Cursor cursor = getContext().getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,null,MoviesContract.MoviesEntry.COLUMN_POSTER_URL + " = ? ",argumentSelection, null);

                if (cursor.getCount() == 0) {

                    myBundle.putString(TITLE, movieTitles[i]);
                    myBundle.putString(RELEASE_DATE, movieRDate[i]);
                    myBundle.putString(VOTE_AVERAGE, movieVAverage[i]);
                    myBundle.putString(OVERVIEW, movieOverview[i]);
                    myBundle.putString(ID, movieIDs[i]);
                }


                myBundle.putString(POSTER_PATH, posterUrls.get(i));
                intent.putExtras(myBundle);
                startActivity(intent);
            }
        });

        myGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {   }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getId() == myGridView.getId()) {
                    currentFirstVisibleItem = myGridView.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        manageToolbar.onHide();

                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem || currentFirstVisibleItem==0) {
                        manageToolbar.onShow();
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;

                }
            }
        });

        return rootView;
    }




    @Override
    public void onTaskCompleted(String[] results) {
        posterUrls.clear();

        if (results != null) {
            for(int i = 0; i<results.length; i++){
                posterUrls.add(results[i]);
            }

            for (String s : results) {
                Log.v(LOG_TAG, "Forecast fullURL: " + s);
            }
            adapter = new ImageListAdapter(getActivity(), results);
            myGridView.setAdapter(adapter);
        }
    }

    @Override
    public void onPause() {
        index = myGridView.getFirstVisiblePosition();
        flag=1;
        super.onPause();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {

            case IMAGES_LOADER_ID:

                Uri queryUri = MoviesContract.MoviesEntry.CONTENT_URI;


                return new CursorLoader(getContext(),
                        queryUri,
                        IMAGES_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if(data.getCount()==0){
                menuFlag=pMenuFlag;
                if(movementFlag==1){
                    LoadingImages defaultTask = new LoadingImages(this, this);
                    defaultTask.execute("", "");
                    Toast.makeText(getContext(), "You don't have any favourite movies anymore!",Toast.LENGTH_SHORT).show();
                    movementFlag=0;
                    return;
                }
                Toast.makeText(getContext(), "You don't have any favourite movies yet!",Toast.LENGTH_SHORT).show();
                return;
            }

            posterUrls.clear();
            for(int i=0; i<data.getCount(); i++){
                data.moveToPosition(i);
                posterUrls.add(data.getString(0));
            }

            adapter = new ImageListAdapter(getActivity(), posterUrls.toArray(new String[posterUrls.size()]));
            myGridView.setAdapter(adapter);
            ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.favourites_label));
            movementFlag=1;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

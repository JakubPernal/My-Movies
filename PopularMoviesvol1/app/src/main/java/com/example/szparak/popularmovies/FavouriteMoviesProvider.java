package com.example.szparak.popularmovies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Szparak on 02.05.2017.
 */

public class FavouriteMoviesProvider extends ContentProvider {


    private MoviesDbHelper mOpenHelper;

    public static final int CODE_MOVIES = 100;
    public static final int CODE_ONE_MOVIE = 101;

    private static final UriMatcher mUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, CODE_MOVIES);
        mUriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES + "/#", CODE_ONE_MOVIE);
        return mUriMatcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        int code = mUriMatcher.match(uri);
        Cursor cursor = null;
        switch (code) {
            case CODE_MOVIES:
                cursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_ONE_MOVIE:

                String movieId = uri.getLastPathSegment();

                String[] argumentSelection = new String[]{movieId};

                cursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ",
                        argumentSelection,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new RuntimeException("Unknown uri " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int code = mUriMatcher.match(uri);
        switch (code) {
            case CODE_ONE_MOVIE:
                db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, contentValues);
                getContext().getContentResolver().notifyChange(uri, null);
        }

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int rowsDeleted = 0;

        String movieId = uri.getLastPathSegment();

        String[] argumentSelection = new String[]{movieId};

        switch (match) {

            case CODE_ONE_MOVIE:

                rowsDeleted = db.delete(MoviesContract.MoviesEntry.TABLE_NAME, MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ", argumentSelection);

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}

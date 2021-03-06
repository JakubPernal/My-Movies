package com.example.szparak.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Szparak on 02.05.2017.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 3;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +

                        MoviesContract.MoviesEntry._ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MoviesContract.MoviesEntry.COLUMN_MOVIE_ID      + " TEXT NO NULL, "                      +

                        MoviesContract.MoviesEntry.COLUMN_POSTER_URL    + " TEXT NO NULL, "                      +

                        MoviesContract.MoviesEntry.COLUMN_TITLE         + " TEXT NOT NULL, "                     +

                        MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE  + " TEXT NOT NULL, "                     +
                        MoviesContract.MoviesEntry.COLUMN_RATE          + " TEXT NOT NULL, "                     +

                        MoviesContract.MoviesEntry.COLUMN_OVERVIEW      + " TEXT NOT NULL, " +


                        " UNIQUE (" + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

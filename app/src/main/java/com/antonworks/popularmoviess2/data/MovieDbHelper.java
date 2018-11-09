package com.antonworks.popularmoviess2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sassa_000 on 23.09.2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 13;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIE_TABLE = "create table "+ MovieContract.MovieEntry.TABLE_NAME + "( " +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT, "+
                MovieContract.MovieEntry.COLUMN_DATE + " TEXT NOT NULL, "+
                MovieContract.MovieEntry.COLUMN_DESC + " TEXT, "+
                MovieContract.MovieEntry.COLUMN_IMAGE_PATH + " TEXT, "+
                MovieContract.MovieEntry.COLUMN_LENGTH + " INTEGER, "+
                MovieContract.MovieEntry.COLUMN_RATING + " REAL, "+
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER"+
                ")";

        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
        {
            String UPDATE_MOVIE_TABLE = "DROP TABLE IF EXISTS "+ MovieContract.MovieEntry.TABLE_NAME;
            db.execSQL(UPDATE_MOVIE_TABLE);
            this.onCreate(db);
        }

    }

}

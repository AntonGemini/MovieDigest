package com.antonworks.popularmoviess2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.UnsupportedSchemeException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by sassa_000 on 26.09.2017.
 */

public class MovieProvider extends ContentProvider {

    private MovieDbHelper dbHelper;
    private static final int CODE_MOVIE = 200;
    private static final int CODE_MOVIE_ID = 201;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher()
    {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.AUTHORITY;
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIES,CODE_MOVIE);
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIES+"/#",CODE_MOVIE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch (sUriMatcher.match(uri))
        {
            case CODE_MOVIE:
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                break;
            case CODE_MOVIE_ID:

                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs,null,null,sortOrder);
                break;
                //throw new Exception("Unknown uri: "+ uri.toString());
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (sUriMatcher.match(uri)==CODE_MOVIE)
        {
            long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
            getContext().getContentResolver().notifyChange(uri,null);
            return ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,id);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri))
        {
            case CODE_MOVIE:
                count = db.delete(MovieContract.MovieEntry.TABLE_NAME,selection,selectionArgs);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

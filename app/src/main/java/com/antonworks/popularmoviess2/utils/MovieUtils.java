package com.antonworks.popularmoviess2.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.antonworks.popularmoviess2.MainActivity;
import com.antonworks.popularmoviess2.Movie;
import com.antonworks.popularmoviess2.NetworkUtils;
import com.antonworks.popularmoviess2.R;
import com.antonworks.popularmoviess2.data.MovieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by sassa_000 on 29.09.2017.
 */

public final class MovieUtils {


    private static Target getTarget(final Context context, final String fileName)
    {
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File file = new File(context.getFilesDir(), fileName);
                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
                            fos.flush();
                            fos.close();
                        }
                        catch (Exception ex)
                        {
                            Log.e(context.getString(R.string.log_tag),ex.getMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e("ERROR_BITMAP",errorDrawable.toString());
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.e("ERROR_BITMAP",placeHolderDrawable.toString());
            }
        };
    }

    public static String[] ConvertStringToReviews(String st)
    {
        String[] reviews = null;
        try {
            JSONObject jsonObject = new JSONObject(st);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            reviews = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject review = jsonArray.getJSONObject(i);
                reviews[i] = review.getString("content");
            }
        }
        catch(JSONException ex) {

        }
        return reviews;
    }

    public static String[] ConvertStringToVideos(String st)
    {
        String[] reviews = null;
        try {
            JSONObject jsonObject = new JSONObject(st);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            reviews = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject review = jsonArray.getJSONObject(i);
                reviews[i] = review.getString("key");
            }
        }
        catch(JSONException ex) {

        }
        return reviews;
    }


    public static Movie[] ConvertStringToMovies(String st)
    {
        Movie[] movieSet = null;
        try {
            JSONObject jsonObject = new JSONObject(st);
            JSONArray jsonArray = jsonObject.getJSONArray("results");

            movieSet = new Movie[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj =jsonArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setId(obj.getInt("id"));
                movie.setThumbPath(obj.getString("poster_path"));
                movie.setTitle(obj.getString("title"));
                movie.setDate(obj.getString("release_date"));
                movie.setDesc(obj.getString("overview"));
                movie.setRating(obj.getDouble("vote_average"));
                movie.setMovieId(obj.getInt("id"));
                movieSet[i] = movie;
            }

        }
        catch (JSONException ex)
        {

        }
        return movieSet;
    }

    public static Movie[] ConvertCursorToMovies(Cursor cursor)
    {
        int count = 0;
        if (cursor != null) count = cursor.getCount();
        Movie[] movieSet = new Movie[count];
        int i = 0;
        while(cursor.moveToNext())
        {

            Movie movie = new Movie();
            movie.setTitle(cursor.getString(MainActivity.INDEX_MOVIE_TITLE));
            movie.setDate(cursor.getString(MainActivity.INDEX_MOVIE_DATE));
            movie.setDesc(cursor.getString(MainActivity.INDEX_MOVIE_DESC));
            movie.setThumbPath(cursor.getString(MainActivity.INDEX_MOVIE_IMAGE_PATH));
            movie.setRating(cursor.getDouble(MainActivity.INDEX_MOVIE_RATING));
            movie.setMovieId(cursor.getInt(MainActivity.INDEX_MOVIE_ID));
            movieSet[i] = movie;
            i++;
        }
        return movieSet;
    }

    public static void toggleFavouriteStatus(Context context, Movie movie, ImageView mFavImageView) {

        if (!movie.getIsFavourite())
        {
            mFavImageView.setImageResource(R.drawable.ic_fav_checked);
            movie.setIsFavourite(true);

            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getDate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_DESC, movie.getDesc());
            contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_PATH, movie.getThumbPath());
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getRating());
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());

            MovieUtils.saveImageToDisk(movie.getThumbPath(), context);
            Uri uri = context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
            Log.d(context.getString(R.string.log_tag), uri.toString());
        }
        else {
            mFavImageView.setImageResource(R.drawable.ic_fav_unchecked);
            movie.setIsFavourite(false);

            String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?";
            String[] selectionArgs = {String.valueOf(movie.getMovieId())};
            int numDeleted = context.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,selection,selectionArgs);
            Log.d("MOVIES_LOG",String.valueOf(numDeleted));
        }
    }

    public static void saveImageToDisk(String fileName, Context context)
    {
        String image_path = context.getString(R.string.medium_image_url,fileName);
        Picasso.with(context).load(image_path).into(getTarget(context,fileName));
    }






}
